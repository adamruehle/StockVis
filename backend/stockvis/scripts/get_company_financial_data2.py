import csv
import os
import yfinance as yf
import argparse
import json
from datetime import datetime

def get_tickers_from_file(filename):
    tickers = []
    with open(filename, 'r') as file:
        reader = csv.DictReader(file)
        for row in reader:
            tickers.append(row['ticker'])
    return tickers

def get_existing_tickers(filename):
    if not os.path.exists(filename):
        return set()
    
    existing_tickers = set()
    with open(filename, 'r') as file:
        reader = csv.DictReader(file)
        for row in reader:
            existing_tickers.add(row['ticker'])
    return existing_tickers

def get_company_financials(ticker):
    try:
        # Create Yahoo Finance ticker object
        stock = yf.Ticker(ticker)
        
        # Get financial data
        info = stock.info
        
        # Format data
        financial_data = {
            "ticker": ticker,
            "date": datetime.now().strftime("%Y-%m-%d"),
            "ebitda": info.get("ebitda"),
            "earningsPerShare": info.get("trailingEps"),
            "profitMargin": info.get("profitMargins"),
            "beta": info.get("beta"),
            "revenue": info.get("totalRevenue"),
            "targetPrice": info.get("targetMeanPrice")
        }
        
        return financial_data
    except Exception as e:
        print(f"Error fetching data for {ticker}: {str(e)}")
        return None

def main():
    input_file = 'csvfiles/all_stocks.csv'
    output_file = 'csvfiles/financial_data.csv'
    
    # Get all tickers
    all_tickers = get_tickers_from_file(input_file)
    
    # Get existing tickers
    existing_tickers = get_existing_tickers(output_file)
    
    # Get tickers that need processing
    tickers_to_process = [t for t in all_tickers if t not in existing_tickers]
    
    # Define fieldnames for CSV
    fieldnames = ['ticker', 'date', 'ebitda', 'earningsPerShare', 'profitMargin', 'beta', 'revenue', 'targetPrice']
    
    # Create or append to output file
    file_exists = os.path.exists(output_file)
    
    # Check if header is present
    header_present = False
    if file_exists:
        with open(output_file, 'r') as file:
            reader = csv.reader(file)
            header_present = any(row for row in reader)
    
    with open(output_file, 'a', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=fieldnames)
        # Write header if not present
        if not header_present:
            writer.writeheader()
        
    # Process each ticker
    for ticker in tickers_to_process:
        print(f"Processing {ticker}...")
        data = get_company_financials(ticker)
        if data:
            with open(output_file, 'a', newline='') as file:
                writer = csv.DictWriter(file, fieldnames=fieldnames)
                writer.writerow(data)
                print(f"Successfully saved data for {ticker}")
                # Flush the file buffer to ensure data is written immediately
                file.flush()

if __name__ == "__main__":
    main()