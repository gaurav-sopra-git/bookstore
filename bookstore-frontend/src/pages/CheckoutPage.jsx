import { useState } from "react";
import { Link } from "react-router-dom";
import {
  ArrowLeft,
  ArrowRight,
  LockKeyhole,
  ChevronRight,
  Home,
} from "lucide-react";
import { money } from "../utils/format";

export default function CheckoutPage({ cart, onSubmit, busy }) {
  const [address, setAddress] = useState("");

  if (!localStorage.getItem("bookstore_access_token")) {
    return (
      <main className="checkout-page">
        <div className="empty-cart-page">
          <h2>Sign in to continue</h2>
          <p>Please sign in before proceeding to checkout.</p>
          <Link to="/" className="button button-dark">
            Return to bookstore
          </Link>
        </div>
      </main>
    );
  }

  if (!cart.items?.length) {
    return (
      <main className="checkout-page">
        <div className="empty-cart-page">
          <h2>Your bag is empty.</h2>
          <p>Add some books before proceeding to checkout.</p>
          <Link to="/" className="button button-dark">
            Explore books <ArrowRight size={16} />
          </Link>
        </div>
      </main>
    );
  }

  return (
    <main className="checkout-page">
      <nav className="page-breadcrumb" aria-label="Breadcrumb">
        <Link to="/">
          <Home size={14} />
          Home
        </Link>
        <span>/</span>

        <ChevronRight size={14} />

        <Link to="/cart">Shopping bag</Link>

        <ChevronRight size={14} />

        <span aria-current="page">Checkout</span>
      </nav>
      <div className="eyebrow">ALMOST YOURS</div>
      <h1>Checkout.</h1>
      <p className="checkout-intro">
        One last step before your next good read.
      </p>

      <div className="checkout-page-layout">
        <section className="checkout-form-section">
          <div className="checkout-section-heading">
            <span>01</span>
            <h2>Delivery details</h2>
          </div>

          <label className="address-label">
            Shipping address
            <textarea
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              maxLength={300}
              rows={4}
              placeholder="House / street, area, city, PIN code"
            />
          </label>

          <div className="checkout-payment-placeholder">
            <div className="checkout-section-heading">
              <span>02</span>
              <h2>Payment</h2>
            </div>

            <div className="payment-option">
              <LockKeyhole size={20} />
              <div>
                <strong>Secure Online payment</strong>
                <p>
                  You will be redirected to Stripe to complete your payment
                  securely.
                </p>
              </div>
            </div>
          </div>

          <Link to="/cart" className="checkout-back-link">
            <ArrowLeft size={15} /> Back to shopping bag
          </Link>
        </section>

        <aside className="checkout-order-summary">
          <h2>Your order</h2>

          {cart.items.map((item) => (
            <div className="summary-row" key={item.book.id}>
              <span>
                {item.book.title} × {item.quantity}
              </span>
              <strong>{money(item.lineTotal)}</strong>
            </div>
          ))}

          <div className="summary-row checkout-grand-total">
            <strong>Order total</strong>
            <strong>{money(cart.subtotal)}</strong>
          </div>

          <button
            className="button button-dark button-full"
            disabled={busy || !address.trim()}
            onClick={() => onSubmit(address.trim())}
          >
            {busy ? "Redirecting to payment..." : "Continue to payment"}
            <ArrowRight size={16} />
          </button>

          <p className="tiny muted">Secure payment powered by Stripe.</p>
        </aside>
      </div>
    </main>
  );
}
