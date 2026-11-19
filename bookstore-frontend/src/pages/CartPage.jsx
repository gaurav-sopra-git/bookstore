import { Link } from "react-router-dom";
import { ArrowRight, ShoppingBag, Trash2, Plus, Minus } from "lucide-react";

import Cover from "../components/BookCover";
import { money } from "../utils/format";
import { Home } from "lucide-react";

export default function CartPage({ cart, onQuantity, onRemove, busy }) {
  return (
    <main className="cart-page">
      <nav className="page-breadcrumb" aria-label="Breadcrumb">
        <Link to="/">
          <Home size={14} />
          Home
        </Link>
        <span>/</span>
        <span>Your shopping bag</span>
      </nav>
      <div className="cart-page-heading">
        <div className="eyebrow">YOUR SELECTION</div>
        <h1>Your shopping bag.</h1>
        <p>{cart.totalItems} items selected</p>
      </div>

      {!cart.items?.length ? (
        <div className="empty-cart-page">
          <ShoppingBag size={42} />
          <h2>Your bag is waiting.</h2>
          <p>Find a book that deserves a place on your shelf.</p>
          <Link to="/" className="button button-dark">
            Explore books <ArrowRight size={16} />
          </Link>
        </div>
      ) : (
        <div className="cart-page-layout">
          <section className="cart-page-items">
            {cart.items.map((item) => (
              <article className="cart-page-item" key={item.book.id}>
                <Link
                  to={`/books/${item.book.id}`}
                  className="book-details-link"
                >
                  <Cover book={item.book} compact index={item.book.id} />
                </Link>

                <div className="cart-page-item-info">
                  <Link
                    to={`/books/${item.book.id}`}
                    className="book-details-link"
                  >
                    <h3>{item.book.title}</h3>
                  </Link>
                  <p>{item.book.author}</p>

                  <strong>{money(item.unitPrice)}</strong>

                  <div className="quantity-control">
                    <button
                      onClick={() =>
                        onQuantity(item.book.id, Math.max(1, item.quantity - 1))
                      }
                      disabled={item.quantity <= 1}
                      aria-label="Decrease quantity"
                    >
                      <Minus size={14} />
                    </button>

                    <span>{item.quantity}</span>

                    <button
                      onClick={() =>
                        onQuantity(item.book.id, item.quantity + 1)
                      }
                      aria-label="Increase quantity"
                    >
                      <Plus size={14} />
                    </button>

                    <button
                      className="remove-button"
                      onClick={() => onRemove(item.book.id)}
                      aria-label="Remove item"
                    >
                      <Trash2 size={15} />
                    </button>
                  </div>
                </div>

                <strong className="cart-page-line-total">
                  {money(item.lineTotal)}
                </strong>
              </article>
            ))}
          </section>

          <aside className="cart-page-summary">
            <h2>Order summary</h2>

            <div className="summary-row">
              <span>Subtotal</span>
              <strong>{money(cart.subtotal)}</strong>
            </div>

            <div className="summary-row muted">
              <span>Shipping</span>
              <span>Calculated at checkout</span>
            </div>

            <div className="summary-row cart-page-total">
              <strong>Total</strong>
              <strong>{money(cart.subtotal)}</strong>
            </div>
            <Link to="/checkout" className="button button-dark button-full">
              Proceed to checkout <ArrowRight size={16} />
            </Link>

            <Link to="/" className="continue-shopping">
              Continue shopping
            </Link>
          </aside>
        </div>
      )}
    </main>
  );
}
