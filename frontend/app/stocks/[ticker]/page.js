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
  const [dividendData, setDividendData] = useState([]); // State for dividend data
  const [financialData, setFinancialData] = useState([]); // State for financial data
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

      const fetchDividendData = async () => {
        try {
          const res = await fetch(
            `http://localhost:8080/api/getDividends?ticker=${ticker}`,
            {
              cache: "no-store", // Prevent caching
            }
          );
          if (!res.ok) {
            throw new Error("Error fetching dividend data");
          }
          const data = await res.json();
          setDividendData(data); 
        } catch (err) {
          console.error(err);
          setError(err.message);
        }
      };

      const fetchCompanyFinancials = async () => {
        try {
          const res = await fetch(
            `http://localhost:8080/api/getCompanyFinancials?ticker=${ticker}`,
            {
              cache: "no-store",
            }
          );
          if (!res.ok) {
            throw new Error("Error fetching financial data");
          }
          const data = await res.json();
          setFinancialData(data);
          console.log(data);
        } catch (err) {
          console.error(err);
          setError(err.message);
        }
      }
      
      fetchStockData();
      fetchDividendData();
      fetchCompanyFinancials();
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
      dayjs(point.date).format("MMM YYYY")
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

  const dividendChartData = {
    labels: dividendData.map((point) =>
      dayjs(point.date).format("MMM YYYY")
    ), // Format dates for better readability
    datasets: [
      {
        label: `${ticker} Dividends`,
        data: dividendData.map((point) => point.dividendAmount), // Extract prices
        fill: true,
        backgroundColor: "#5400dc55",
        borderColor: "#5400dc",
      },
    ],
  };

  const dividendChartData2 = {
    labels: dividendData.map((point) =>
      dayjs(point.date).format("MMM YYYY")
    ), // Format dates for better readability
    datasets: [
      {
        label: `${ticker} Div Yield`,
        data: dividendData.map((point) => point.dividendYield), // Extract prices
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
        display: true,
        
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
          <h1 className="font-bold text-5xl">{stockData.company.name}</h1>
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

        

          <div>
            <h2 className="text-3xl font-bold my-5">Dividend History</h2>
            <div className="flex justify-between">
            <div className="w-1/2 inline-block">
            <Line data={dividendChartData} options={chartOptions} />
            </div>
            <div className="w-1/2 inline-block">
            <Line data={dividendChartData2} options={chartOptions} />
            </div>
            </div>

        
          </div>
          <div>
            <h1 className="text-3xl font-bold my-5">Company Wide Financials</h1>
            <div className="overflow-x-auto">
              <table className="min-w-full text-xl inline-block w rounded-lg overflow-hidden">
                <tbody>
                  <tr className="border-b border-accent">
                    <td className="py-3 px-6 font-semibold">Founded Year</td>
                    <td className="py-3 px-6">{financialData[0].company?.foundedYear || 'N/A'}</td>
                  </tr>
                  <tr className="border-b border-accent">
                    <td className="py-3 px-6 font-semibold">Headquarters</td>
                    <td className="py-3 px-6">{financialData[0].company?.headquarters || 'N/A'}</td>
                  </tr>
                  <tr className="border-b border-accent">
                    <td className="py-3 px-6 font-semibold">Industry</td>
                    <td className="py-3 px-6">{financialData[0].company?.industry || 'N/A'}</td>
                  </tr>
                  <tr className="border-b border-accent">
                    <td className="py-3 px-6 font-semibold">Sector</td>
                    <td className="py-3 px-6">{financialData[0].company?.sector || 'N/A'}</td>
                  </tr>
                  <tr className="border-b border-accent">
                    <td className="py-3 px-6 font-semibold">Beta</td>
                    <td className="py-3 px-6">{financialData[0].beta}</td>
                  </tr>
                  <tr className="border-b border-accent">
                    <td className="py-3 px-6 font-semibold">Earnings Per Share</td>
                    <td className="py-3 px-6">${financialData[0].earningsPerShare}</td>
                  </tr>
                  <tr className="border-b border-accent">
                    <td className="py-3 px-6 font-semibold">EBITDA</td>
                    <td className="py-3 px-6">${(financialData[0].ebitda / 1000000000).toFixed(2)}B</td>
                  </tr>
                  <tr className="border-b border-accent">
                    <td className="py-3 px-6 font-semibold">Profit Margin</td>
                    <td className="py-3 px-6">{(financialData[0].profitMargin * 100).toFixed(2)}%</td>
                  </tr>
                  <tr className="border-b border-accent">
                    <td className="py-3 px-6 font-semibold">Revenue</td>
                    <td className="py-3 px-6">${(financialData[0].revenue / 1000000000).toFixed(2)}B</td>
                  </tr>
                  <tr>
                    <td className="py-3 px-6 font-semibold">Target Price</td>
                    <td className="py-3 px-6">${financialData[0].targetPrice}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
       
      </main>
    </div>
  );
}
