import Image from "next/image";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faLineChart, faApple } from '@fortawesome/free-solid-svg-icons'

export default function Home() {
  return (
    <div className="text-lg">
      <header className="nav-grid p-3 bg-gray-800">
        <div className="flex text-2xl">
          <div className="">
          <FontAwesomeIcon icon={faLineChart} size="lg" />
          </div>
          <div className="mx-3 self-end">
            StockVis
          </div>
        </div>
        
        <nav className="self-end">
          <ul className="flex justify-around ">
            <li className=" ">Home</li>
            <li>Stock Data</li>
            <li>Filter & Insights</li>
          </ul>

        </nav>
      </header>
      <main className="p-64">
        <h1 className="text-center text-3xl">Welcome to StockVis</h1>
        <p className="my-3 text-center">Discover powerful insights and data visualization tools designed to help you make informed stock market decisions. With StockVis, navigate through an intuitive platform tailored for both beginners and experts</p>
        <button className="block bg-white p-4 font-bold rounded-2xl text-black m-auto">Explore Stock Data</button>
      </main>
      <footer className="p-7 bg-gray-800 flex justify-between">
        <div>
          <h2 className="text-2xl font-bold">Quick Links</h2>
          <ul>
            <li>Home</li>
            <li>Stock Data</li>
            <li>Filter & Insights</li>
          </ul>
        </div>
        <div>
          <h2 className="text-2xl font-bold">Contact Us</h2>
          <ul>
            <li>Email: support@stockvis.com</li>
            <li>Phone: (000)-000-0000</li>
          </ul>
        </div>
        <div>
          <h2 className="text-2xl font-bold">Follow Us</h2>
          <ul>
          </ul>
        </div>
      </footer>
    </div>
  );
}
