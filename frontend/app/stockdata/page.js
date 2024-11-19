import Image from "next/image";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faLineChart, faApple } from '@fortawesome/free-solid-svg-icons'
import Link from "next/link";

export default function StockData() {
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
      <main className="flex-grow container mx-auto p-48">
        
      </main>
    </div>
);
}
