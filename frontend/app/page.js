import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faLineChart, faApple } from '@fortawesome/free-solid-svg-icons'
import Link from "next/link";

export default function Home() {
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
                <Link href="/stocks" className="hover:text-white">Stock Data</Link>
              </li>
              <li>
                <Link href="/insights" className="hover:text-white">Filter & Insights</Link>
              </li>
            </ul>
          </nav>
        </div>
      </header>
      <main className="flex-grow container mx-auto p-48">
        <h1 className="text-center text-4xl font-bold mb-6 ">Welcome to StockVis</h1>
        <p className="text-center text-lg mb-8">
          Discover powerful insights and data visualization tools designed to help you make informed stock market decisions. With StockVis, navigate through an intuitive platform tailored for both beginners and experts.
        </p>
        <div className="flex justify-center">
          <Link href="/stocks">
            <button className="bg-white text-black px-6 py-3 rounded-full font-semibold hover:bg-accent transition duration-300">
              Explore Stock Data
            </button>
          </Link>
        </div>
      </main>
      <footer className="bg-accent text-white p-8">
        <div className="container mx-auto grid grid-cols-1 md:grid-cols-3 gap-8">
          <div>
            <h2 className="text-xl font-bold mb-4">Quick Links</h2>
            <ul>
              <li className="mb-2">
                <Link href="/" className="hover:text-gray-300">Home</Link>
              </li>
              <li className="mb-2">
                <Link href="/stocks" className="hover:text-gray-300">Stock Data</Link>
              </li>
              <li className="mb-2">
                <Link href="/insights" className="hover:text-gray-300">Filter & Insights</Link>
              </li>
            </ul>
          </div>
          <div>
            <h2 className="text-xl font-bold mb-4">Contact Us</h2>
            <ul>
              <li className="mb-2">Email: support@stockvis.com</li>
              <li className="mb-2">Phone: (000)-000-0000</li>
            </ul>
          </div>
          <div>
            <h2 className="text-xl font-bold mb-4">Follow Us</h2>
            <ul className="flex space-x-4">
              <li>
                <a href="#" className="hover:text-gray-300">Twitter</a>
              </li>
              <li>
                <a href="#" className="hover:text-gray-300">Facebook</a>
              </li>
              <li>
                <a href="#" className="hover:text-gray-300">LinkedIn</a>
              </li>
            </ul>
          </div>
        </div>
      </footer>
    </div>
  );
}
