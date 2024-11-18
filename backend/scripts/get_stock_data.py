import requests
from datetime import datetime, timezone, timedelta
import json
import csv
from bs4 import BeautifulSoup
import undetected_chromedriver as uc
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium_stealth import stealth
import os
import time
import pandas as pd
import re

def start_session():
  session = requests.Session()
  session.headers = {
    'Accept': 'application/json, text/plain, */*',
    'Accept-Encoding': 'gzip, deflate',
    'Accept-Language': 'en-US,en;q=0.9',
    'User-Agent': ('Mozilla/5.0 (Windows NT 10.0; Win64; x64)'
                    ' AppleWebKit/537.36 (KHTML, like Gecko)'
                    ' Chrome/115.0.0.0 Safari/537.36'),
  }
  return session

def start_driver():
  options = uc.ChromeOptions()
  options.add_argument("--headless")  # Remove this if you want to see the browser for testing
  options.add_argument("--disable-gpu")
  options.add_argument("--no-sandbox")
  options.add_argument("--disable-blink-features=AutomationControlled")
  options.add_argument("--disable-infobars")
  options.add_argument("--start-maximized")
  driver = uc.Chrome(options=options)
  stealth(driver,
        languages=["en-US", "en"],
        vendor="Google Inc.",
        platform="Win32",
        webgl_vendor="Intel Inc.",
        renderer="Intel Iris OpenGL Engine",
        fix_hairline=True,
        )
  return driver

def generate_data_url(tickers, range_param, interval):
  tickers_str = ",".join(tickers)
  url = (f"https://query1.finance.yahoo.com/v7/finance/spark?symbols={tickers_str}"
          f"&range={range_param}&interval={interval}&indicators=close"
          "&includeTimestamps=true&includePrePost=false&corsDomain=finance.yahoo.com"
          "&.tsrc=finance")
  # print(f"Requesting URL: {url}")
  return url

def get_response(session, url):
  response = session.get(url)
  try:
    response.raise_for_status()
    return response.json()
  except requests.exceptions.HTTPError as e:
    print(f"HTTP error occurred: {e}")
    print(f"Response status code: {response.status_code}")
    print(f"Response text: {response.text}")
    return None
  except json.decoder.JSONDecodeError as e:
    print(f"JSON decode error occurred: {e}")
    print(f"Response status code: {response.status_code}")
    print(f"Response text: {response.text}")
    return None

def get_all_stocks_from_file():
  with open('all_stocks.csv', 'r') as file:
    reader = csv.reader(file)
    header = next(reader)
    stock_tickers = [row[0] for row in reader]
  return stock_tickers

def convert_timestamp(ts, gmtoffset):
  tz = timezone(timedelta(seconds=gmtoffset))
  dt = datetime.fromtimestamp(ts, tz)
  return dt.strftime('%Y-%m-%d')

def extract_data(data):
  all_stock_data = []
  if data is None:
    return all_stock_data
  for stock in data.get('spark', {}).get('result', []):
    try:
      response = stock['response'][0]
      meta = response['meta']
      gmtoffset = meta.get('gmtoffset', 0)
      ticker = meta['symbol']
      # Check if 'timestamp' and 'close' data are available
      if ('timestamp' not in response or
          'indicators' not in response or
          'quote' not in response['indicators'] or
          not response['indicators']['quote'] or
          'close' not in response['indicators']['quote'][0] or
          not response['indicators']['quote'][0]['close']):
          print(f"No data available for symbol: {ticker}")
          continue  # Skip this stock
      timestamps = response['timestamp']
      prices = response['indicators']['quote'][0]['close']
      # Convert timestamps
      readable_times = [convert_timestamp(ts, gmtoffset) for ts in timestamps]
      # Combine times and prices
      stock_entries = list(zip(readable_times, prices))
      # Append the stock data to the list
      all_stock_data.append({'ticker': ticker, 'data': stock_entries})
      # print(f"Retrieved data for symbol: {ticker}")
    except Exception as e:
      print(f"Error processing symbol {stock.get('symbol', 'Unknown')}: {e}")
      continue  # Skip to the next stock
  return all_stock_data

def save_stock_prices(ticker_prices, filename='ticker_prices.csv'):
  with open(filename, 'w', newline='') as file:
    writer = csv.writer(file)
    writer.writerow(['Ticker', 'Price'])  # Write header
    for ticker, price in ticker_prices.items():
      writer.writerow([ticker, price])

def extract_stock_prices(session, stock_tickers, range_param='5y', interval='1d', chunk_size=20):
  """
  Extract stock data for the given tickers over the specified date range and interval.

  Parameters:
  - session: The requests session to use for API calls.
  - stock_tickers: A list of stock ticker symbols to retrieve data for.
  - range_param: The range parameter for the API call (e.g., '1d', '5d', '1mo', '5y', etc.).
  - interval: The interval parameter for the API call (e.g., '1d', '1wk', '1mo').
  - chunk_size: The number of tickers to process per API call.

  Returns:
  - all_stock_data: A list containing the extracted data for all tickers.
  """
  all_stock_data = []

  # Process tickers in chunks
  split_stock_tickers_list = [
    stock_tickers[i:i + chunk_size] for i in range(0, len(stock_tickers), chunk_size)
  ]

  for tickers in split_stock_tickers_list:
    # print(f"Processing tickers: {tickers}")
    url = generate_data_url(tickers, range_param, interval)
    data = get_response(session, url)
    extracted_data = extract_data(data)
    all_stock_data.extend(extracted_data)
  print(f"Total stocks retrieved: {len(all_stock_data)}")
  return all_stock_data  # Optionally, save the data here

def extract_all_company_data_for_tickers():
  driver = start_driver()
  session = start_session()
  stock_tickers = get_all_stocks_from_file()
  all_company_data = []
  # Load existing stock data
  fieldnames = ['ticker', 'first_trade_date', 'long_name', 'short_name', 'sector', 'industry', 'address', 'website']
  if not os.path.exists('all_company_data.csv'):
    with open('all_company_data.csv', 'w', newline='', encoding='utf-8') as csvfile:
      writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
      writer.writeheader()
  with open('all_company_data.csv', 'r') as csvfile:
    reader = csv.DictReader(csvfile)
    existing_data = [item['ticker'] for item in reader]
  # Remove already processed tickers
  stock_tickers = [ticker for ticker in stock_tickers if ticker not in existing_data]
  # Create groups of 20 tickers to process
  for i in range(0, len(stock_tickers), 20):
    chunk = stock_tickers[i:i + 20]
    url = generate_data_url(chunk, '5m', '5m')
    data = get_response(session, url)
    # Extract the meaningful data
    for stock in data.get('spark', {}).get('result', []):
      try:
        response = stock['response'][0]
        first_trade_date = response['meta'].get('firstTradeDate', 'Unknown')
        long_name = response['meta'].get('longName', 'Unknown')
        short_name = response['meta'].get('shortName', 'Unknown')
        company_data = {
          'ticker': stock['response'][0]['meta']['symbol'],
          'first_trade_date': first_trade_date,
          'long_name': long_name,
          'short_name': short_name,
        }
        all_company_data.append(company_data)
      except Exception as e:
        print(f"Error processing symbol {stock.get('symbol', 'Unknown')}: {e}")
        continue
  # Iterate through each stock ticker and get url
  for ticker in stock_tickers:
    url = f"https://finance.yahoo.com/quote/{ticker}/profile/"
    driver.get(url)
    # Get the page source
    page_source = driver.page_source
    soup = BeautifulSoup(page_source, 'html.parser')
    # Exctract the sector and industry information
    try:
      # Find company details
      company_details = soup.find('div', class_='company-details')
      if not company_details:
        print(f"Company details not found for {ticker}")
        continue
      # Extract address
      address_div = company_details.find('div', class_='address')
      if address_div:
        address = ' '.join([line.get_text(strip=True) for line in address_div.find_all('div')])
      else:
        address = ""
      # Extract website
      website_element = company_details.find('a', {'aria-label': 'website link'})
      website = website_element.get('href') if website_element else None
      # Extract company stats
      company_stats = company_details.find('dl', class_='company-stats')
      if not company_stats:
        print(f"Company stats not found for {ticker}")
        continue
      sector = ""
      industry = ""
      employee_count = ""
      # Iterate over all 'div' elements within 'company_stats'
      for item in company_stats.find_all('div'):
        dt = item.find('dt')
        if dt:
          label = dt.get_text(strip=True)
          if label == 'Sector:':
            dd = item.find('dd')
            if dd:
              a = dd.find('a')
              if a:
                sector = a.get_text(strip=True)
          elif label == 'Industry:':
            a = item.find('a')
            if a:
              industry = a.get_text(strip=True)
    except Exception as e:
      print(f"Error processing symbol {ticker}: {e}")
      print(company_details.prettify())
      exit()
    # Append the extracted data to the list
    print(f"Ticker: {ticker}, Sector: {sector}, Industry: {industry}")
    for company_data in all_company_data:
      if company_data['ticker'] == ticker:
        company_data.update({
          'sector': sector,
          'industry': industry,
          'address': address,
          'website': website,
        })
        break
    # print(company_data)
    with open('all_company_data.csv', 'a', newline='', encoding='utf-8') as csvfile:
      writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
      writer.writerow(company_data)
    time.sleep(5)
  return all_company_data

def extract_all_balance_sheets_for_tickers(sheet_type):
  driver = start_driver()
  stock_tickers = get_all_stocks_from_file()
  csv_file = 'all_balance_sheets.csv' if sheet_type == 'balance' else 'all_income_statements.csv'
  url_sheet_type = 'balance-sheet' if sheet_type == 'balance' else 'financials'
  # Initialize an empty DataFrame if CSV doesn't exist
  if not os.path.exists(csv_file):
    df = pd.DataFrame()
    existing_tickers = set()
  else:
    # Read existing CSV into DataFrame
    df = pd.read_csv(csv_file)
    existing_tickers = set(df['ticker'])
  for ticker in stock_tickers:
    if ticker in existing_tickers:
      print(f"Ticker {ticker} already processed. Skipping.")
      continue
    url = f"https://finance.yahoo.com/quote/{ticker}/{url_sheet_type}"
    print(f"Processing ticker: {ticker}")
    driver.get(url)
    # Wait for the page to load
    try:
      # Wait up to 10 seconds for the table to load
      WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.CSS_SELECTOR, 'div.tableContainer'))
      )
      time.sleep(1)  # Additional wait to ensure the table is fully loaded
    except Exception as e:
      print(f"Timeout or error loading page for ticker {ticker}")
      # Add an empty row for failed ticker processing
      empty_row = {'ticker': ticker, 'date': ''}
      df = pd.concat([df, pd.DataFrame([empty_row])], ignore_index=True)
      df.to_csv(csv_file, index=False)
      continue
    soup = BeautifulSoup(driver.page_source, 'html.parser')
    # Extract column headers (dates)
    header_row = soup.find('div', class_='tableHeader')
    if not header_row:
      print(f"No header row found for ticker {ticker}")
      continue
    # Get the first div with class='column'
    most_recent_date = header_row.find_all('div', class_='column')[1].get_text(strip=True)
    print(most_recent_date)
    # Prepare a dictionary to hold the data for this ticker
    data_dict = {'ticker': ticker, 'date': most_recent_date}
    # Function to normalize labels
    def normalize_label(label):
      return label.lower().replace(' ', '_').replace('-', '_').replace('/', '_').replace("'", '').strip()
    # Find all rows in the table body
    table_body = soup.find('div', class_='tableBody')
    if not table_body:
      print(f"No table body found for ticker {ticker}")
      continue
    table_rows = table_body.find_all('div', class_='row')
    if not table_rows:
      print(f"No balance sheet data found for ticker {ticker}")
      continue
    for row in table_rows:
      columns = row.find_all('div', class_='column', recursive=False)
      if len(columns) < 2:
        continue  # Skip if there's no data
      row_label = columns[0].get_text(strip=True)
      normalized_row_label = normalize_label(row_label)
      value = columns[1].get_text(strip=True)
      value = value.replace(',', '')  # Remove commas from numbers
      if value == "--":
        value = ""
      data_dict[normalized_row_label] = value
      print(f"{normalized_row_label}: {value}")
    # Convert data_dict to DataFrame
    df_new = pd.DataFrame([data_dict])
    # Merge with existing DataFrame, aligning columns
    df = pd.concat([df, df_new], ignore_index=True, sort=False)
    # Update existing_tickers
    existing_tickers.add(ticker)
    # Save DataFrame to CSV after each ticker
    df.to_csv(csv_file, index=False)
    time.sleep(5)
  driver.quit()

if __name__ == "__main__":
  # session = start_session()
  # stock_tickers = get_all_stocks_from_file()
  # all_stock_data = extract_stock_prices(session, stock_tickers)
  extract_all_balance_sheets_for_tickers(sheet_type='income')
  time.sleep(300)
  extract_all_company_data_for_tickers()