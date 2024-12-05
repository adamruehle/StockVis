import argparse
import requests
import json

def get_company_financial_data(stock_ticker, api_key):
    """
    Fetches company financial data using Alpha Vantage API.
    
    Parameters:
        stock_ticker (str): Stock ticker symbol of the company.
        api_key (str): Your Alpha Vantage API key.

    Returns:
        dict: A dictionary containing the company's financial data.
    """
    base_url = "https://www.alphavantage.co/query"
    params = {
        "function": "OVERVIEW",
        "symbol": stock_ticker,
        "apikey": api_key
    }

    try:
        response = requests.get(base_url, params=params)
        response.raise_for_status()  # Raise HTTPError for bad responses (4xx or 5xx)
        data = response.json()

        if "Error Message" in data:
            print("Error: Invalid stock ticker or API issue.")
            return None

        if not data:
            print("No financial data found for the given stock ticker.")
            return None

        return data
    except requests.exceptions.RequestException as e:
        print(f"An error occurred: {e}")
        return None

# Example usage
if __name__ == "__main__":
    API_KEY = "6MMXPI0RHTPW9OK1"
    parser = argparse.ArgumentParser(description='Get company financial data for given ticker.')
    parser.add_argument('ticker', help='Stock ticker symbol')
    args = parser.parse_args()

    # private Double ebitda;
    # private Double earningsPerShare;
    # private Double profitMargin;
    # private Double beta;
    # private Double revenue;
    # private Double targetPrice;
    
    data = get_company_financial_data(args.ticker, API_KEY)
    # Only get these values
    edited_data = {
        "date": data.get("LatestQuarter"),
        "ebitda": data.get("EBITDA"),
        "earningsPerShare": data.get("EPS"),
        "profitMargin": data.get("ProfitMargin"),
        "beta": data.get("Beta"),
        "revenue": data.get("RevenueTTM"),
        "targetPrice": data.get("AnalystTargetPrice")
    }
    if edited_data:
        formatted_data = {
            "financial_data": edited_data
        }
        print(json.dumps(formatted_data))
    else:
        print(json.dumps({"error": "No data found"}))
