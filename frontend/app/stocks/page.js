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
    <main className="flex-grow container p-10">
    <h1 className="text-5xl">Stocks</h1>
    <div className="flex flex-wrap">
      {stocks.map((stock) => (
      <div key={stock.ticker} className="p-4 border m-2">
        <h2 className="text-xl font-bold">{stock.ticker}</h2>
        <p>Current Price: ${stock.currentPrice}</p>
      </div>
      ))}
    </div>
    </main>
  </div>
);
}
