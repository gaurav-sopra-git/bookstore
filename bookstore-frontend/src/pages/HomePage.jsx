import {
  ArrowRight,
  BookOpen,
  CreditCard,
  Truck,
  LibraryBig,
  Search,
  Plus,
} from "lucide-react";
import { Link } from "react-router-dom";

import Cover from "../components/BookCover";
import { money } from "../utils/format";

export default function HomePage({
  books,
  query,
  setQuery,
  loadBooks,
  loading,
  busy,
  addBook,
}) {
  return (
    <main>
      <section className="hero">
        <div className="hero-copy">
          <div className="eyebrow">
            <span className="eyebrow-line" /> BOOKS FOR THE CURIOUS
          </div>
          <h1>
            Stories worth
            <br />
            <em>staying up for.</em>
          </h1>
          <p>
            A considered collection of books to spark ideas, shift perspectives,
            and keep you turning pages.
          </p>
          <a className="button button-dark" href="#catalogue">
            Explore the collection <ArrowRight size={16} />
          </a>
          <div className="hero-note">
            <span className="avatar-stack">
              <i>R</i>
              <i>M</i>
              <i>A</i>
            </span>
            <span>Loved by readers who love to read.</span>
          </div>
        </div>
        <div className="hero-art" aria-label="A curated stack of books">
          <div className="sun-disc" />
          <div className="floating-note">
            A little more
            <br />
            wonder, daily.
          </div>
          <div className="stack-book stack-one">
            <span>
              THE ART OF
              <br />
              NOTICING
            </span>
            <small>FIELD NOTES FOR LIFE</small>
          </div>
          <div className="stack-book stack-two">
            <span>
              THINK
              <br />
              DEEPER.
            </span>
            <small>IDEAS THAT STAY</small>
          </div>
          <div className="stack-book stack-three">
            <span>
              BETWEEN
              <br />
              THE LINES
            </span>
            <small>STORIES & THOUGHTS</small>
          </div>
          <div className="hero-stamp">
            <BookOpen size={17} />
            <span>
              READ
              <br />
              MORE
            </span>
          </div>
        </div>
      </section>

      <section className="benefit-strip">
        <div>
          <Truck size={18} />
          <span>Thoughtful delivery</span>
        </div>
        <div>
          <LibraryBig size={18} />
          <span>Curated reading list</span>
        </div>
        <div>
          <CreditCard size={18} />
          <span>Secure checkout</span>
        </div>
      </section>

      <section className="catalogue-section" id="catalogue">
        <div className="section-heading">
          <div>
            <div className="eyebrow">THE BOOKSHELF</div>
            <h2>
              Find your next <em>favourite.</em>
            </h2>
          </div>
          <div className="catalogue-tools">
            <label className="search-box">
              <Search size={17} />
              <input
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="Search title or author"
              />
              <button onClick={loadBooks} aria-label="Search">
                <ArrowRight size={15} />
              </button>
            </label>
          </div>
        </div>
        <div className="result-line">
          <span>
            {query ? `Results for “${query}”` : "A few good places to begin"}
          </span>
          <span>
            {books.length} {books.length === 1 ? "book" : "books"}
          </span>
        </div>
        {loading ? (
          <div className="loading-state">Gathering the good reads…</div>
        ) : books.length === 0 ? (
          <div className="empty-catalogue">
            <BookOpen size={30} />
            <h3>No books found</h3>
            <p>Try another title or author.</p>
            <button
              className="button button-outline"
              onClick={() => setQuery("")}
            >
              Clear search
            </button>
          </div>
        ) : (
          <div className="book-grid">
            {books.map((book, index) => (
              <article className="book-card" key={book.id}>
                <div className="book-card-art">
                  <Link to={`/books/${book.id}`} className="book-details-link">
                    <Cover book={book} index={index} />
                  </Link>
                  <span className="card-number">0{index + 1}</span>
                </div>
                <div className="book-meta">
                  <div>
                    <Link
                      to={`/books/${book.id}`}
                      className="book-details-link"
                    >
                      <h3>{book.title}</h3>
                    </Link>
                    <p>{book.author}</p>
                  </div>
                  <strong>{money(book.price)}</strong>
                </div>
                <p className="book-description">{book.description}</p>
                <div className="book-card-footer">
                  <span className={book.stock > 0 ? "stock-ok" : "stock-out"}>
                    {book.stock > 0 ? `${book.stock} in stock` : "Out of stock"}
                  </span>
                  <button
                    className="add-button"
                    disabled={book.stock <= 0 || busy}
                    onClick={() => addBook(book)}
                  >
                    <Plus size={15} /> Add to bag
                  </button>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>

      <section className="quote-section" id="about">
        <span className="quote-mark">“</span>
        <p>A room without books is like a body without a soul.</p>
        <span className="quote-credit">— CICERO</span>
      </section>
    </main>
  );
}
