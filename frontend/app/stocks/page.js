"use client";

import Link from "next/link";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLineChart } from "@fortawesome/free-solid-svg-icons";
import { useEffect, useState } from "react";

export default function StockData() {
  const [stocks, setStocks] = useState([]);
  const [nyseStocks, setNyseStocks] = useState([]);
  const [nasdaqStocks, setNasdaqStocks] = useState([]);
  const [filteredStocks, setFilteredStocks] = useState([]);

  useEffect(() => {
    fetchStocks();
    fetchNyseStocks();
    fetchNasdaqStocks();
  }, []);

  const fetchStocks = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/getTopStocks?limit=10");
      const data = await res.json();
      setStocks(data);
    } catch (error) {
      console.error(error);
    }
  };

  const fetchNyseStocks = async () => {
    try {
      const res = await fetch(
        "http://localhost:8080/api/getTopStocksByExchange?limit=10&exchange=XNYS"
      );
      const data = await res.json();
      setNyseStocks(data);
    } catch (error) {
      console.error(error);
    }
  };

  const fetchNasdaqStocks = async () => {
    try {
      const res = await fetch(
        "http://localhost:8080/api/getTopStocksByExchange?limit=10&exchange=XNGS"
      );
      const data = await res.json();
      setNasdaqStocks(data);
    } catch (error) {
      console.error(error);
    }
  };

  const fetchFilteredStocks = async (query) => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/getStocks?tickerString=${query}`
      );
      const data = await res.json();
      setFilteredStocks(data);
    } catch (error) {
      console.error(error);
    }
  };

  const duplicateList = (list) => [...list, ...list]; // Duplicate the list for seamless scrolling

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
                <Link href="/" className="hover:text-white">
                  Home
                </Link>
              </li>
              <li>
                <Link href="/stocks" className="hover:text-white">
                  Stock Data
                </Link>
              </li>
              <li>
                <Link href="/macro" className="hover:text-white">
                  Economic Data
                </Link>
              </li>
              <li>
                <Link href="/insights" className="hover:text-white">
                  Filter & Insights
                </Link>
              </li>
            </ul>
          </nav>
        </div>
      </header>
      <main className="p-10">
        <div className="flex justify-between items-center">
        <h1 className="text-5xl">Stocks</h1>
        <p className="bg-accent p-3 rounded-xl text-xl">Click on any stock to see more details</p>
        </div>
        
        {/* Top 10 by Market Cap */}
        <section className="my-10">
          <h2 className="text-3xl">Top 10 largest stocks by Market Cap</h2>
          <div className="auto-scroll overflow-hidden my-5">
            <div className="auto-scroll-content">
              {duplicateList(stocks).map((stock, index) => (
                <Link
                  href={`/stocks/${stock.ticker}`}
                  className="hover:bg-accent transition duration-300 p-4 border m-2 min-w-max "
                  key={index}
                >
                  <div className="">
                    <h2 className="text-2xl font-bold">{stock.ticker}</h2>
                    <p>Current Price: ${stock.currentPrice}</p>
                  </div>
                </Link>
              ))}
            </div>
          </div>
        </section>
        <hr className="border-accent"/>
        <div className="flex justify-between w-full">
        {/* NYSE Stocks */}
        <section className="border-r border-accent pr-5 my-5 w-1/2">
          <h2 className="text-3xl">Top 10 stocks in NYSE</h2>
          <div className="auto-scroll overflow-hidden my-5">
            <div className="auto-scroll-content">
              {duplicateList(nyseStocks).map((stock, index) => (
                <Link
                  href={`/stocks/${stock.ticker}`}
                  className="hover:bg-accent transition duration-300 p-4 border m-2 min-w-max "
                  key={index}
                >
                  <div className="">
                    <h2 className="text-2xl font-bold">{stock.ticker}</h2>
                    <p>Current Price: ${stock.currentPrice}</p>
                  </div>
                </Link>
              ))}
            </div>
          </div>
        </section>


        {/* NASDAQ Stocks */}
        <section className="ml-5 my-5 w-1/2">
          <h2 className="text-3xl">Top 10 stocks in NASDAQ</h2>
          <div className="auto-scroll overflow-hidden my-5">
            <div className="auto-scroll-content">
              {duplicateList(nasdaqStocks).map((stock, index) => (
                <Link
                  href={`/stocks/${stock.ticker}`}
                  className="hover:bg-accent transition duration-300 p-4 border m-2 min-w-max"
                  key={index}
                >
                  <div className="">
                    <h2 className="text-2xl font-bold">{stock.ticker}</h2>
                    <p>Current Price: ${stock.currentPrice}</p>
                  </div>
                </Link>
              ))}
            </div>
          </div>
        </section>
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
                <li
                  key={stock.ticker}
                  className="hover:bg-accent transition duration-300 text-2xl border-b border-accent py-4"
                >
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
