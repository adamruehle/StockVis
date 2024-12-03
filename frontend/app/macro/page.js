"use client";

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faLineChart } from '@fortawesome/free-solid-svg-icons'
import Link from "next/link";
import { useEffect, useState } from "react";
import { Line } from 'react-chartjs-2';
import 'chart.js/auto';
import { text } from '@fortawesome/fontawesome-svg-core';

export default function StockData() {
  const [economicData, setEconomicData] = useState([]);
  const [selectedDataType, setSelectedDataType] = useState("gdp"); // Track selected option
  const [chartData, setChartData] = useState(null); // Dynamic chart data

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {
    if (economicData.length > 0) {
      updateChartData(selectedDataType); // Update chart data whenever the selected data type changes
    }
  }, [selectedDataType, economicData]);

  const fetchData = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/getEconomicData");
      const data = await res.json();
      setEconomicData(data);
      updateChartData("gdp"); // Default to GDP initially
    } catch (error) {
      console.error(error);
    }
  };

  const updateChartData = (dataType) => {
    const fieldMapping = {
      gdp: "GDP",
      "inflationRate": "Inflation Rate",
      "unemploymentRate": "Unemployment Rate",
      "interestRate": "Interest Rates",
    };

    setChartData({
      labels: economicData.map((data) => data.date),
      datasets: [
        {
          label: fieldMapping[dataType],
          data: economicData.map((data) => data[dataType]),
          fill: false,
          backgroundColor: "#5400dc",
          borderColor: "#5400dc",
          pointColor: "#5400dc",
        },
      ],
    });
  };

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
        <h1 className="text-5xl">Economic Data</h1>
        <div className="mt-8">
          <label
            htmlFor="economic-data"
            className="block text-lg font-medium"
          >
            Select Economic Data:
          </label>
          <select
            id="economic-data"
            name="economic-data"
            className="mt-2 block w-full p-10 text-lg text-black rounded-md border-accent"
            onChange={(e) => setSelectedDataType(e.target.value)} // Update selected data type
          >
            <option value="gdp" className="text-black">GDP</option>
            <option value="inflationRate" className="text-black">Inflation Rate</option>
            <option value="unemploymentRate" className="text-black">Unemployment Rate</option>
            <option value="interestRate" className="text-black">Interest Rates</option>
          </select>
        </div>

        {chartData ? (
          <div className="mt-8">
            <Line data={chartData} />
          </div>
        ) : (
          <div className="mt-8">
            <p>Loading...</p>
          </div>
        )}
      </main>
    </div>
  );
}
