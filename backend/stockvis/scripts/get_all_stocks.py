import requests
import csv

def get_all_stocks():
  session = requests.Session()
  # Access the initial page to get cookies
  url = 'https://www.nyse.com/listings_directory/stock'
  session.get(url)
  # Define headers (excluding the cookie header, as the session handles cookies)
  headers = {
    'accept': '*/*',
    'accept-encoding': 'gzip, deflate, br, zstd',
    'accept-language': 'en-US,en;q=0.9',
    'cache-control': 'no-cache',
    'connection': 'keep-alive',
    'content-type': 'application/json',
    'host': 'www.nyse.com',
    'origin': 'https://www.nyse.com',
    'pragma': 'no-cache',
    'referer': 'https://www.nyse.com/listings_directory/stock',
    'sec-ch-ua': '"Google Chrome";v="131", "Chromium";v="131", "Not_A Brand";v="24"',
    'sec-ch-ua-mobile': '?0',
    'sec-ch-ua-platform': '"Windows"',
    'sec-fetch-dest': 'empty',
    'sec-fetch-mode': 'cors',
    'sec-fetch-site': 'same-origin',
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36',
  }
  # Initialize variables
  stocks = []
  pageNumber = 1
  maxResultsPerPage = 10  # Adjust as needed (max is typically 100)
  total_results = None
  unique_tickers = set()
  # Loop to retrieve all pages, though there should only be one page with
  # less than 10000 results
  while True:
    payload = {
      "instrumentType": "EQUITY",
      "pageNumber": pageNumber,
      "sortColumn": "NORMALIZED_TICKER",
      "sortOrder": "ASC",
      "maxResultsPerPage": maxResultsPerPage,
      "filterToken": ""
    }
    response = session.post(
      'https://www.nyse.com/api/quotes/filter',
      headers=headers,
      json=payload
    )
    if response.status_code != 200:
      print(f"Failed to retrieve data for page {pageNumber}")
      break
    data = response.json()
    if not data:
      # No more data available
      break
    # Get total results from the first item on the first page
    if total_results is None:
      total_results = data[0]['total']
      print(f"Total results to fetch: {total_results}")
    for item in data:
      if item["symbolTicker"] in unique_tickers:
        # Skip duplicate entries
        continue
      unique_tickers.add(item["symbolTicker"])
      if item["instrumentType"] != "COMMON_STOCK":
        # Skip non-common stock instruments
        continue
      stock_info = {
        'ticker': item['symbolTicker'].replace('.', '-'),
        'company_name': item['instrumentName'],
        'mic': item['micCode'],
      }
      stocks.append(stock_info)
    print(f"Retrieved page {pageNumber}")
    pageNumber += 1
    # Check if all results have been retrieved
    if len(stocks) >= total_results:
        break
  return stocks

def save_stocks_to_file(stocks, filename):
  with open(filename, 'w', newline='') as csvfile:
    writer = csv.DictWriter(csvfile, fieldnames=stocks[0].keys())
    writer.writeheader()
    for stock in stocks:
      writer.writerow(stock)

if __name__ == "__main__":
    all_stocks = get_all_stocks()
    print(f"Total stocks retrieved: {len(all_stocks)}")
    # You can save the data to a file or process it as needed
    save_stocks_to_file(all_stocks, 'all_stocks.csv')
    print(len(all_stocks))
