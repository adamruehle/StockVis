
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faLineChart, faApple } from '@fortawesome/free-solid-svg-icons'
import Link from "next/link";

export default async function StockDetails({ params }) {
    const { ticker } = await params;
  
    const res = await fetch(`http://localhost:8080/api/getStocks?tickerString=${ticker}`, {
      cache: "no-store", // Prevent caching for fresh data
    });
  
    if (!res.ok) {
      return <div>Error fetching stock data</div>;
    }
  
    const stocks = await res.json();
    const stock = stocks[0];
  
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
                  <Link href="/" className="hover:text-white">Home</Link>
                </li>
                <li>
                  <Link href="/stocks" className="hover:text-white">Stock Data</Link>
                </li>
                <li>
                  <Link href="/macro" className="hover:text-white">Economic Data</Link>
                </li>
                <li>
                  <Link href="/insights" className="hover:text-white">Filter & Insights</Link>
                </li>
              </ul>
            </nav>
          </div>
        </header>
        <main className='p-14'>
          <div className='flex justify-between'>
            <h1 className='font-bold text-5xl'>{stock.company}</h1>
            <h1 className='font-bold text-5xl'>{stock.ticker}</h1>
          </div>
          <p>Current Price:</p>
        </main>
      </div>
    );
  }
  