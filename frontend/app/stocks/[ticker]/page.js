

export default async function StockDetails({ params }) {
    const { ticker } = await params;
  
    const res = await fetch(`http://localhost:8080/api/getStock?ticker=${ticker}`, {
      cache: "no-store", // Prevent caching for fresh data
    });
  
    if (!res.ok) {
      return <div>Error fetching stock data</div>;
    }
  
    const stock = await res.json();
  
    return (
      <div>
        <h1>{stock.name} ({stock.ticker})</h1>
        <p>Current Price: ${stock.currentPrice}</p>
      </div>
    );
  }
  