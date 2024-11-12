import requests
from bs4 import BeautifulSoup

url = "https://stockanalysis.com/list/sp-500-stocks/"

def get_sp500():
  response = requests.get(url)
  soup = BeautifulSoup(response.text, "html.parser")
  # Find all tr's with class "svelte-eurwtr"
  rows = soup.find_all("tr", class_="svelte-eurwtr")
  for row in rows:
    if "Symbol Company Name" in row.text:
      continue # Skip the header row
    # Find the a tag in each row, which contains the stock symbol
    a_tag = row.find("a")
    print(a_tag.text)

if __name__ == "__main__":
  get_sp500()
