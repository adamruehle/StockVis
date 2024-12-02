import pandas as pd
from fredapi import Fred
import os
import csv

def get_fred_data(api_key, series_ids):
    fred = Fred(api_key=api_key)
    start_date = '1900-01-01'
    end_date = '2024-12-01'
    dates = pd.date_range(start=start_date, end= end_date)
    econ_data = pd.DataFrame(index = dates)
    for id in series_ids:
        temp_df = fred.get_series(id, start_date, end_date)
        if id == 'CPILFESL':
            temp_df = temp_df.pct_change(periods=1)*100
        temp_df.name = id
        temp_df = temp_df.to_frame()
        econ_data = econ_data.join(temp_df, how = 'left')
    econ_data = econ_data.dropna(how = 'all')
    print(econ_data)
    return econ_data

def save_macro_data_to_file(data, filename):
    script_dir = os.path.dirname(os.path.abspath(__file__)) 
    file_path = os.path.join(script_dir, filename)
    data.columns = ['GDP (In Billions)', 'Unemployment Rate (%)', 'Interest Rate (%)', 'Inflation Rate (%)' ]
    data.to_csv(file_path)
    print('file has been written')
    
if __name__ == "__main__":
    api_key = '65c759ce023209f366fb4ae5db10160a'
    series_ids = ['GDP', 'UNRATE', 'FEDFUNDS', 'CPILFESL']
    data = get_fred_data(api_key, series_ids)
    save_macro_data_to_file(data, 'macro_data.csv')

