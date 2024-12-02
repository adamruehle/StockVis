import csv
from datetime import datetime, timedelta
from pytz import timezone
import requests
import json
import argparse

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
    print("Response status code:", response.status_code)
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
  with open('scripts/all_stocks.csv', 'r') as file:
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

def extract_stock_prices(session, stock_tickers, range_param='1d', interval='1d', chunk_size=20):
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

  # Prepare the output CSV file
  output_file = "stockvis/scripts/stock_prices.csv"
  with open(output_file, 'w', newline='') as csvfile:
    writer = csv.writer(csvfile)
    writer.writerow(['Ticker', 'Datetime', 'Price'])  # Write the header
    # Process tickers in chunks
    split_stock_tickers_list = [
      stock_tickers[i:i + chunk_size] for i in range(0, len(stock_tickers), chunk_size)
    ]
    for tickers in split_stock_tickers_list:
      print(tickers)
      # Generate API URL
      url = generate_data_url(tickers, range_param, interval)
      data = get_response(session, url)
      extracted_data = []
      # Parse the API response
      if data and 'spark' in data and 'result' in data['spark']:
        for stock in data['spark']['result']:
          ticker = stock.get('symbol', 'Unknown')
          responses = stock.get('response', [])
          for resp in responses:
            timestamps = resp.get('timestamp', [])
            quotes = resp.get('indicators', {}).get('quote', [{}])[0].get('close', [])
            # Extract and format data
            for ts, price in zip(timestamps, quotes):
              # Convert timestamp to datetime
              date_time = datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')
              extracted_data.append([ticker, date_time, price])
      # Append to all_stock_data and write to CSV
      all_stock_data.extend(extracted_data)
      writer.writerows(extracted_data)
  print(f"Data written to {output_file}")
  print(f"Total stocks retrieved: {len(all_stock_data)}")
  return all_stock_data

def main(stock_tickers):
  # Start a new session
  session = start_session()
  # Extract stock prices
  all_stock_data = extract_stock_prices(session, stock_tickers, range_param='1d', interval='1d', chunk_size=20)
  # Return the extracted stock prices
  return all_stock_data

if __name__ == "__main__":
  parser = argparse.ArgumentParser(description='Get stock prices for given tickers.')
  parser.add_argument('tickers', nargs='+', help='List of stock tickers')
  args = parser.parse_args()
  # Extract stock data
  all_stock_data = main(args.tickers)
  # Print the data as JSON
  print(json.dumps(all_stock_data))

  # # Get all stock tickers
  # stock_tickers = get_all_stocks_from_file()
  # # Extract stock data
  # session = start_session()
  # extract_stock_prices(session, stock_tickers, range_param='365d', interval='1d', chunk_size=20)
