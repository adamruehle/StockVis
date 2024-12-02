import yfinance as yf
import os
import pandas as pd
import csv
import time 

stock_list_path = 'all_stocks.csv'


def get_stocks(file_path):
    print(os.getcwd())
    script_dir = os.path.dirname(os.path.abspath(__file__))
    file_path = os.path.join(script_dir, 's&p_stocks.csv')
    if not os.path.exists(file_path):
        print(f"Error: The data directory '{file_path}' does not exist.")
        return pd.DataFrame()  # Return an empty DataFrame if the path does not exist
    stock_df = pd.read_csv(file_path, index_col= 'Symbol')
    stock_list = stock_df.index.to_list()
    return stock_list

def get_dividend_data(stock_list):
    dividend_data = []
    start_date = '2020-01-01'
    end_date = '2024-12-01'
    dates = pd.date_range(start=start_date, end= end_date)
    for symbol in stock_list:
        stock = yf.Ticker(symbol)
        dividends = stock.dividends
        dividends.index = pd.to_datetime(dividends.index)
        dividends = dividends[start_date:end_date]
        
        time_deltas = dividends.index.to_series().diff().dt.days

        average_interval = time_deltas.mean()

        if average_interval <= 95:  # ~3 months
            frequency = "Quarterly"
        elif average_interval <= 200:  # ~6 months
            frequency = "Semi-Annually"
        elif average_interval <= 400:  # ~12 months
            frequency = "Annually"
        else:
            frequency = "Irregular"
            
        if not dividends.empty:
            for date, amount in dividends.items():
                price_data = stock.history(start=date, end=date + pd.Timedelta(days=1))
                if price_data.empty:
                    print(f"No data found for {symbol}")
                else:
                    try: close_price = price_data['Close'].iloc[0] 
                    except (IndexError, KeyError): close_price = None
                    dividend_yield = amount / close_price if close_price else None
                    dividend_tuple = (symbol, date.date(), close_price, amount, dividend_yield, frequency )
                    print(dividend_tuple)
                    dividend_data.append(dividend_tuple)
        time.sleep(1)
    return dividend_data

def save_dividends_to_file(dividends, filename):
    script_dir = os.path.dirname(os.path.abspath(__file__)) 
    file_path = os.path.join(script_dir, filename)
    with open(file_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        header = ['Stock', 'Date', 'Stock Price', 'Dividend Amount', 'Dividend Yield']
        writer.writerow(header)  # Write header
        writer.writerows(dividends)
        print('file has been written')
        
    
if __name__ == "__main__":
    stocks = get_stocks(stock_list_path)
    data = get_dividend_data(stocks)
    save_dividends_to_file(data, 'all_dividends.csv')
    
    