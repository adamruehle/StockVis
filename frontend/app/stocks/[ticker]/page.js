"use client";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLineChart } from "@fortawesome/free-solid-svg-icons";
import { useEffect, useState } from "react";
import Link from "next/link";
import { Line } from "react-chartjs-2";
import "chart.js/auto";
import dayjs from "dayjs";
import { Colors } from "chart.js/auto";

export default function StockDetails({ params }) {
  const [ticker, setTicker] = useState(null); // State for unwrapped ticker
  const [stockData, setStockData] = useState(null); // State to store stock data
  const [priceHistory, setPriceHistory] = useState([]); // State for price history
  const [error, setError] = useState(null); // State for error handling

  useEffect(() => {
    // Unwrap `params` using `React.use()` or set the ticker directly if params is resolved
    const fetchParams = async () => {
      const unwrappedParams = await params;
      setTicker(unwrappedParams.ticker);
    };

    fetchParams();
  }, [params]);

  useEffect(() => {
    if (ticker) {
      // Fetch stock data and price history
      const fetchStockData = async () => {
        try {
          const res = await fetch(
            `http://localhost:8080/api/getStockPrices?ticker=${ticker}`,
            {
              cache: "no-store", // Prevent caching
            }
          );
          if (!res.ok) {
            throw new Error("Error fetching stock data");
          }
          const data = await res.json();
          setStockData(data[0]?.stock || null); // Extract the nested stock object
          setPriceHistory(data); // Store entire price history
        } catch (err) {
          console.error(err);
          setError(err.message);
        }
      };

      fetchStockData();
    }
  }, [ticker]);

  if (error) {
    return <div>Error: {error}</div>;
  }

  if (!ticker || !stockData) {
    return <div>Loading...</div>;
  }

  // Prepare data for the line chart
  const chartData = {
    labels: priceHistory.map((point) =>
      dayjs(point.date).format("MMM DD, YYYY HH:mm")
    ), // Format dates for better readability
    datasets: [
      {
        label: `${ticker} Stock Price`,
        data: priceHistory.map((point) => point.currentPrice), // Extract prices
        fill: true,
        backgroundColor: "#5400dc55",
        borderColor: "#5400dc",
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    plugins: {
      legend: {
        display: false,
        position: "top",
        labels: {
          color: "white",
          fontSize: 20,
        }
      },
    },
    scales: {
      x: {
        title: {
          color: "white",
          display: true,
          text: "Date",
        },
        ticks: {
          color: "white",
          maxTicksLimit: 12, // Limit the number of ticks for better readability
        },
      
      },
      y: {
        title: {
          color: "white",
          display: true,
          text: "Price (USD)",
        },
        ticks: {
          color: "white",
        },
      },
    },
  };

  return (
    <div>
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
      <main className="p-14">
        <div className="flex justify-between">
          <h1 className="font-bold text-5xl">{stockData.company}</h1>
          <h1 className="font-bold text-5xl">{stockData.ticker}</h1>
        </div>
        <p className="mt-4 text-4xl">
          $
          {priceHistory.length > 0
            ? priceHistory[priceHistory.length - 1].currentPrice
            : "N/A"}
        </p>

        {/* Line Chart */}
        <div className="mt-10">
          <h2 className="text-3xl font-bold mb-5">Stock Price History</h2>
          <Line data={chartData} options={chartOptions} />
        </div>
      </main>
    </div>
  );
}
