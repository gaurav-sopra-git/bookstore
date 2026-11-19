import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";

import { getBookById } from "../api";
import { money } from "../utils/format";
import Cover from "../components/BookCover";
import { Plus, Minus, Home } from "lucide-react";

export default function BookDetails({ addBook, busy }) {
  const { id } = useParams();

  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [quantity, setQuantity] = useState(1);

  useEffect(() => {
    let ignore = false;

    const fetchBook = async () => {
      try {
        setLoading(true);
        setError("");
        setBook(null);
        setQuantity(1);

        const response = await getBookById(id);

        if (!ignore) {
          setBook(response);
        }
      } catch (err) {
        if (!ignore) {
          console.error("Failed to fetch book:", err);
          setError("Unable to load book details.");
        }
      } finally {
        if (!ignore) {
          setLoading(false);
        }
      }
    };

    fetchBook();

    return () => {
      ignore = true;
    };
  }, [id]);

  if (loading) {
    return (
      <main className="product-page">
        <div className="loading-state">Loading book details...</div>
      </main>
    );
  }

  if (error || !book) {
    return (
      <main className="product-page">
        <div className="empty-catalogue">
          <h2>Book not found</h2>
          <p>{error || "This book may no longer be available."}</p>
        </div>
      </main>
    );
  }
  return (
    <main className="product-page">
      <nav className="page-breadcrumb" aria-label="Breadcrumb">
        <Link to="/">
          {" "}
          <Home size={14} />
          Home
        </Link>
        <span>/</span>
        <Link to="/#catalogue">Books</Link>
        <span>/</span>
        <span>{book.title}</span>
      </nav>

      <section className="pdp-layout">
        <div className="pdp-cover">
          <Cover book={book} index={0} />
        </div>

        <div className="pdp-info">
          <div className="eyebrow">THE BOOKWORM COLLECTION</div>

          <h1>{book.title}</h1>

          <p className="pdp-author">By {book.author}</p>

          <div className="pdp-price">{money(book.price)}</div>

          <div className="pdp-stock">
            {book.stock > 0 ? (
              <span className="stock-ok">{book.stock} copies available</span>
            ) : (
              <span className="stock-out">Currently unavailable</span>
            )}
          </div>

          <div className="pdp-description">
            <h3>About this book</h3>
            <p>{book.description}</p>
          </div>

          {book.stock > 0 && (
            <div className="pdp-quantity">
              <label>Quantity</label>

              <div className="quantity-control">
                <button
                  onClick={() =>
                    setQuantity((current) => Math.max(1, current - 1))
                  }
                  disabled={quantity <= 1}
                  aria-label="Decrease quantity"
                >
                  <Minus size={16} />
                </button>

                <span>{quantity}</span>

                <button
                  onClick={() =>
                    setQuantity((current) => Math.min(book.stock, current + 1))
                  }
                  disabled={quantity >= book.stock}
                  aria-label="Increase quantity"
                >
                  <Plus size={16} />
                </button>
              </div>
            </div>
          )}

          <button
            className="add-button pdp-add-button"
            disabled={book.stock <= 0 || busy}
            onClick={() => addBook(book, quantity)}
          >
            <Plus size={16} />
            {busy ? "Adding..." : "Add to bag"}
          </button>
        </div>
      </section>

      <section className="pdp-reviews">
        <div className="eyebrow">WHAT READERS THINK</div>
        <h2>Reader reviews</h2>
        <p>Customer reviews are coming soon. Stay tuned!</p>
      </section>
    </main>
  );
}
