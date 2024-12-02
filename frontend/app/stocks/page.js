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
    <main className=" p-10">
      <h1 className="text-5xl">Stocks</h1>
      {stocks.length > 0 && (
        <p className="my-3 text-xl">Last Updated: {new Date(stocks[0].date).toLocaleDateString()}</p>
      )}
      
      <div className="my-5 flex overflow-x-auto w-auto">
        {stocks.map((stock) => (
          <Link className="p-6 border m-4 min-w-max" key={stock.ticker} href={`/stocks/AAPL`}>
          <div key={stock.ticker} >
            <h2 className="text-xl font-bold">{stock.ticker}</h2>
            <p>Current Price: ${stock.currentPrice}</p>
          </div>
          </Link>
        ))}
      </div>
      <div className="my-5">
        <input
          type="text"
          placeholder="Search by ticker..."
          value={searchQuery}
          onChange={(e) => {
        setSearchQuery(e.target.value);
        const filtered = stocks.filter(stock => 
          stock.ticker.toLowerCase().includes(e.target.value.toLowerCase())
        );
        setFilteredStocks(filtered);
        getPrice(e.target.value);
          }}
          className="p-2 border rounded w-full"
        />
      </div>
    </main>
  </div>
);
}
