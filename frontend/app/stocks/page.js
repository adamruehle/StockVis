"use client";

import Image from "next/image";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faLineChart, faApple } from '@fortawesome/free-solid-svg-icons'
import Link from "next/link";
import { useEffect, useState, } from "react";


export default function StockData() {

  const [stocks, setStocks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [filteredStocks, setFilteredStocks] = useState([]);

  useEffect(() => {
    fetchStocks();  
  }, []);

  const fetchStocks = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/getTopMarketCaps")
      const data = await res.json();
      setStocks(data);
    }
    catch (error) {
      console.error(error);
    }
  }

  const fetchFilteredStocks = async (query) => {
    try {
      const res = await fetch(`http://localhost:8080/api/getStocks?tickerString=${query}`)
      const data = await res.json();
      setFilteredStocks(data);
    }
    catch (error) {
      console.error(error);
    }
  }
return (
  <div className="min-h-screen flex flex-col">
    <header className="bg-accent p-4 shadow-md">
      <div className="container mx-auto flex justify-between items-center">
        <div className="flex items-center">
          <FontAwesomeIcon icon={faLineChart} size="lg" />
          <span className="ml-3 text-2xl font-bold">StockVis</span>
        </div>
        <nav>
          <ul className="flex space-x-4">
            <li>
              <Link href="/" className="hover:text-white">Home</Link>
            </li>
            <li>
              <Link href="/stockdata" className="hover:text-white">Stock Data</Link>
            </li>
            <li>
              <Link href="/insights" className="hover:text-white">Filter & Insights</Link>
            </li>
          </ul>
        </nav>
      </div>
    </header>
    <main className="p-10">
      <h1 className="text-5xl">Stocks</h1>
      {stocks.length > 0 && (
        <p className="my-3 text-xl">Last Updated: {new Date(stocks[0].date).toLocaleDateString()}</p>
      )}
      
      <div className="my-5 flex overflow-x-auto w-auto">
        {stocks.map((stock) => (
          <Link className="p-6 border m-4 min-w-max" key={stock.ticker} href={`/stocks/${stock.ticker}`}>
            <div key={stock.ticker} className="text-2xl">
              <h2 className="text-3xl font-bold">{stock.ticker}</h2>
              <p>Current Price: ${stock.currentPrice}</p>
            </div>
          </Link>
        ))}
      </div>
      <div className="my-5">
        <input
          type="text"
          placeholder="Search by ticker..."
          onChange={(e) => {
            if (e.target.value.length >= 2) {
              fetchFilteredStocks(e.target.value);
            }
          }}
          className="p-4 border text-black rounded w-full text-2xl"
        />
      </div>
      {filteredStocks.length > 0 && (
        <ul>
          {filteredStocks.map((stock) => (
            <Link key={stock.ticker} href={`/stocks/${stock.ticker}`}>
            <li key={stock.ticker} className="text-2xl border-b-2 border-gray-300 py-4">
              {stock.ticker} - {stock.company}
            </li>
            </Link>
          ))}
        </ul>
      )}
    </main>
  </div>
);
}
