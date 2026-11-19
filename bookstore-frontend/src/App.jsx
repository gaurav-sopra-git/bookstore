import { useCallback, useEffect, useMemo, useState } from "react";
import { Routes, Route, useNavigate } from "react-router-dom";
import Cover from "./components/BookCover";
import { money } from "./utils/format";
import HomePage from "./pages/HomePage";
import BookDetails from "./pages/BookDetails";
import CartPage from "./pages/CartPage";
import { Link } from "react-router-dom";
import CheckoutPage from "./pages/CheckoutPage";
import PaymentResultPage from "./pages/PaymentResultPage";
import {
  ArrowRight,
  BookOpen,
  Check,
  ChevronDown,
  ChevronLeft,
  ChevronRight,
  CircleUserRound,
  CreditCard,
  Minus,
  Plus,
  Search,
  ShoppingBag,
  Trash2,
  Truck,
  X,
  LogOut,
  LibraryBig,
  Sparkles,
  Sun,
  Moon,
} from "lucide-react";
import {
  addCartItem,
  checkout,
  createGuestCart,
  getBooks,
  getCart,
  getOrders,
  login,
  register,
  removeCartItem,
  searchBooks,
  updateCartItem,
  initiatePayment,
} from "./api";

function AuthModal({ mode, onClose, onSuccess, busy, setBusy, setNotice }) {
  const [kind, setKind] = useState(mode || "login");
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  async function submit(event) {
    event.preventDefault();
    setBusy(true);
    try {
      const result =
        kind === "register"
          ? await register(name.trim(), email.trim(), password)
          : await login(email.trim(), password);
      onSuccess(result);
      onClose();
    } catch (error) {
      setNotice({ type: "error", text: error.message });
    } finally {
      setBusy(false);
    }
  }

  return (
    <div
      className="modal-backdrop"
      onMouseDown={(e) => e.target === e.currentTarget && onClose()}
    >
      <section className="modal auth-modal">
        <button
          className="icon-button modal-close"
          onClick={onClose}
          aria-label="Close"
        >
          <X />
        </button>
        <div className="eyebrow">YOUR READING JOURNEY</div>
        <h2>{kind === "login" ? "Welcome back." : "Join the book club."}</h2>
        <p className="muted">
          {kind === "login"
            ? "Sign in to continue to checkout and view your orders."
            : "Create an account to place orders and keep track of your books."}
        </p>
        <div className="auth-tabs">
          <button
            className={kind === "login" ? "active" : ""}
            onClick={() => setKind("login")}
          >
            Sign in
          </button>
          <button
            className={kind === "register" ? "active" : ""}
            onClick={() => setKind("register")}
          >
            Create account
          </button>
        </div>
        <form onSubmit={submit} className="form-stack">
          {kind === "register" && (
            <label>
              Full name
              <input
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
                maxLength={120}
                placeholder="Your name"
              />
            </label>
          )}
          <label>
            Email address
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              placeholder="you@example.com"
            />
          </label>
          <label>
            Password
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={kind === "register" ? 8 : 1}
              placeholder={
                kind === "register" ? "At least 8 characters" : "Your password"
              }
            />
          </label>
          <button className="button button-dark button-full" disabled={busy}>
            {busy
              ? "Please wait…"
              : kind === "login"
                ? "Sign in"
                : "Create account"}{" "}
            <ArrowRight size={16} />
          </button>
        </form>
      </section>
    </div>
  );
}

function CartDrawer({
  open,
  onClose,
  cart,
  onQuantity,
  onRemove,
  onCheckout,
  busy,
}) {
  if (!open) return null;
  return (
    <div
      className="drawer-backdrop"
      onMouseDown={(e) => e.target === e.currentTarget && onClose()}
    >
      <aside className="cart-drawer">
        <div className="drawer-head">
          <div>
            <div className="eyebrow">YOUR SELECTION</div>
            <h2>
              Your bag{" "}
              <span className="count-pill">{cart?.totalItems || 0}</span>
            </h2>
          </div>
          <button
            className="icon-button"
            onClick={onClose}
            aria-label="Close cart"
          >
            <X />
          </button>
        </div>
        {!cart?.items?.length ? (
          <div className="empty-state">
            <ShoppingBag size={34} />
            <h3>Your bag is waiting.</h3>
            <p className="muted">
              Find a book that deserves a place on your shelf.
            </p>
            <button className="button button-outline" onClick={onClose}>
              Explore books
            </button>
          </div>
        ) : (
          <>
            <div className="cart-lines">
              {cart.items.map((item) => (
                <div className="cart-line" key={item.book.id}>
                  <Link
                    to={`/books/${item.book.id}`}
                    className="book-details-link"
                  >
                    <Cover book={item.book} compact index={item.book.id} />
                  </Link>
                  <div className="cart-line-info">
                    <Link
                      to={`/books/${item.book.id}`}
                      className="book-details-link"
                    >
                      <strong>{item.book.title}</strong>
                    </Link>
                    <span className="muted">{item.book.author}</span>
                    <strong className="line-price">
                      {money(item.lineTotal)}
                    </strong>
                    <div className="quantity-control">
                      <button
                        onClick={() =>
                          onQuantity(
                            item.book.id,
                            Math.max(1, item.quantity - 1),
                          )
                        }
                        aria-label="Decrease quantity"
                      >
                        <Minus size={13} />
                      </button>
                      <span>{item.quantity}</span>
                      <button
                        onClick={() =>
                          onQuantity(item.book.id, item.quantity + 1)
                        }
                        aria-label="Increase quantity"
                      >
                        <Plus size={13} />
                      </button>
                      <button
                        className="remove-button"
                        onClick={() => onRemove(item.book.id)}
                        aria-label="Remove item"
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
            <div className="cart-summary">
              <div className="summary-row">
                <span>Subtotal</span>
                <strong>{money(cart.subtotal)}</strong>
              </div>
              <div className="summary-row muted">
                <span>Shipping</span>
                <span>Calculated at checkout</span>
              </div>
              <button
                className="button button-dark button-full"
                onClick={onCheckout}
                disabled={busy}
              >
                Continue to checkout <ArrowRight size={16} />
              </button>
              <p className="tiny muted">
                Taxes and delivery details are confirmed before your order is
                placed.
              </p>
            </div>
          </>
        )}
      </aside>
    </div>
  );
}

function CheckoutModal({ cart, onClose, onSubmit, busy }) {
  const [address, setAddress] = useState("");
  return (
    <div
      className="modal-backdrop"
      onMouseDown={(e) => e.target === e.currentTarget && onClose()}
    >
      <section className="modal checkout-modal">
        <button
          className="icon-button modal-close"
          onClick={onClose}
          aria-label="Close"
        >
          <X />
        </button>
        <div className="eyebrow">ALMOST YOURS</div>
        <h2>Checkout</h2>
        <p className="muted">Add a delivery address and review your order.</p>
        <label className="address-label">
          Shipping address
          <textarea
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            maxLength={300}
            rows={3}
            placeholder="House / street, area, city, PIN code"
          />
        </label>
        <div className="checkout-review">
          {cart.items.map((item) => (
            <div className="summary-row" key={item.book.id}>
              <span>
                {item.book.title} × {item.quantity}
              </span>
              <strong>{money(item.lineTotal)}</strong>
            </div>
          ))}
          <div className="summary-row total-row">
            <strong>Order total</strong>
            <strong>{money(cart.subtotal)}</strong>
          </div>
        </div>
        <button
          className="button button-dark button-full"
          disabled={busy || !address.trim()}
          onClick={() => onSubmit(address.trim())}
        >
          {busy ? "Placing order…" : "Place order"} <ArrowRight size={16} />
        </button>
        <p className="tiny muted">Demo checkout: no payment is collected.</p>
      </section>
    </div>
  );
}

export default function App() {
  const [books, setBooks] = useState([]);
  const [cart, setCart] = useState({ items: [], totalItems: 0, subtotal: 0 });
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(true);
  const [cartOpen, setCartOpen] = useState(false);
  const [authOpen, setAuthOpen] = useState(false);
  const [authMode, setAuthMode] = useState("login");
  const [checkoutOpen, setCheckoutOpen] = useState(false);
  const [busy, setBusy] = useState(false);
  const [notice, setNotice] = useState(null);
  const navigate = useNavigate();
  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem("bookstore_user") || "null");
    } catch {
      return null;
    }
  });
  const [orders, setOrders] = useState([]);
  const [ordersOpen, setOrdersOpen] = useState(false);
  const [appearance, setAppearance] = useState(
    () => localStorage.getItem("appearance") || "light",
  );

  const toggleAppearance = () => {
    setAppearance((current) => (current === "light" ? "dark" : "light"));
  };

  useEffect(() => {
    localStorage.setItem("appearance", appearance);
  }, [appearance]);

  const refreshCart = useCallback(async () => {
    try {
      setCart(await getCart());
    } catch (error) {
      setNotice({
        type: "error",
        text: `Could not load cart: ${error.message}`,
      });
    }
  }, []);

  const ensureGuestCart = useCallback(async () => {
    if (!localStorage.getItem("bookstore_cart_token")) {
      const created = await createGuestCart();
      if (created?.cartToken)
        localStorage.setItem("bookstore_cart_token", created.cartToken);
    }
  }, []);

  const loadBooks = useCallback(async () => {
    setLoading(true);
    try {
      const data = query.trim()
        ? await searchBooks(query.trim())
        : await getBooks();
      setBooks(Array.isArray(data) ? data : data?.content || []);
    } catch (error) {
      setNotice({
        type: "error",
        text: `Could not load books: ${error.message}`,
      });
    } finally {
      setLoading(false);
    }
  }, [query]);

  useEffect(() => {
    (async () => {
      try {
        await ensureGuestCart();
        await Promise.all([loadBooks(), refreshCart()]);
      } catch (error) {
        setNotice({ type: "error", text: error.message });
        setLoading(false);
      }
    })();
  }, [ensureGuestCart, loadBooks, refreshCart]);

  useEffect(() => {
    if (!notice) return;
    const timer = setTimeout(() => setNotice(null), 4800);
    return () => clearTimeout(timer);
  }, [notice]);

  async function addBook(book, quantity = 1) {
    setBusy(true);
    try {
      const updated = await addCartItem(book.id, quantity);
      setCart(updated);
      setNotice({
        type: "success",
        text: `${quantity} × ${book.title} added to your bag.`,
      });
    } catch (error) {
      setNotice({ type: "error", text: error.message });
    } finally {
      setBusy(false);
    }
  }

  async function changeQuantity(bookId, quantity) {
    try {
      setCart(await updateCartItem(bookId, quantity));
    } catch (error) {
      setNotice({ type: "error", text: error.message });
    }
  }

  async function removeBook(bookId) {
    try {
      setCart(await removeCartItem(bookId));
    } catch (error) {
      setNotice({ type: "error", text: error.message });
    }
  }

  async function handleAuthSuccess(result) {
    localStorage.setItem("bookstore_access_token", result.accessToken);
    localStorage.setItem("bookstore_user", JSON.stringify(result.user));
    setUser(result.user);
    // Transfer the anonymous cart to the authenticated cart, item by item.
    const guestItems = cart.items || [];
    try {
      let latest = await getCart();
      for (const item of guestItems) {
        latest = await addCartItem(item.book.id, item.quantity);
      }
      setCart(latest);
      setNotice({
        type: "success",
        text: `Welcome${result.user?.name ? `, ${result.user.name}` : ""}!`,
      });
    } catch (error) {
      setNotice({
        type: "error",
        text: `Signed in, but cart transfer needs attention: ${error.message}`,
      });
    }
  }

  function signOut() {
    localStorage.removeItem("bookstore_access_token");
    localStorage.removeItem("bookstore_user");
    setUser(null);
    setOrders([]);
    setOrdersOpen(false);
    refreshCart();
    setNotice({ type: "success", text: "You have signed out." });
  }

  async function beginCheckout() {
    if (!cart.items?.length) return;
    setCartOpen(false);
    if (!user) {
      setAuthMode("login");
      setAuthOpen(true);
      setNotice({
        type: "info",
        text: "Sign in or create an account to continue checkout.",
      });
      return;
    }
    navigate("/checkout");
  }

  async function placeOrder(address) {
    setBusy(true);
    try {
      const order = await checkout(address);
      const payment = await initiatePayment(order.orderNumber);
      if (!payment?.checkoutUrl) {
        throw new Error("Could not create Stripe checkout session.");
      }
      window.location.assign(payment.checkoutUrl);
      /*   setCheckoutOpen(false);
      setCart({ items: [], totalItems: 0, subtotal: 0 });
      setNotice({
        type: "success",
        text: `Order ${order.orderNumber} placed successfully.`,
      });
      setOrders((previous) => [order, ...previous]);
      navigate("/");*/
    } catch (error) {
      setNotice({ type: "error", text: error.message });
      setBusy(true);
    }
  }

  async function showOrders() {
    if (!user) {
      setAuthMode("login");
      setAuthOpen(true);
      return;
    }
    try {
      setOrders(await getOrders());
      setOrdersOpen(true);
    } catch (error) {
      setNotice({ type: "error", text: error.message });
    }
  }

  const itemCount = cart.totalItems || 0;

  return (
    <div className="app-shell" data-theme={appearance}>
      <div className="announcement">
        <Sparkles size={13} /> A good book makes an ordinary day feel
        extraordinary.
      </div>
      <header className="site-header">
        <a
          className="brand"
          href="#"
          onClick={(e) => {
            e.preventDefault();
            setOrdersOpen(false);
          }}
        >
          <Link to="/" className="brand-icon">
            <BookOpen size={19} />
          </Link>
          <Link to="/" className="brand-period">
            bookworm<span className="brand-period">.</span>
            <small>THE INDEPENDENT BOOKSHOP</small>
          </Link>
        </a>
        <nav className="main-nav">
          <a href="#catalogue">Discover</a>
          <a href="#about">Our story</a>
        </nav>
        <div className="header-actions">
          {user ? (
            <button className="user-chip" onClick={showOrders}>
              <CircleUserRound size={17} />
              <span>{user.name || user.email}</span>
              <ChevronDown size={14} />
            </button>
          ) : (
            <button
              className="text-button"
              onClick={() => {
                setAuthMode("login");
                setAuthOpen(true);
              }}
            >
              Sign in
            </button>
          )}
          {user && (
            <button className="text-button signout" onClick={signOut}>
              <LogOut size={15} /> Sign out
            </button>
          )}
          <div className="bag-button">
            <Link
              to="/cart"
              className="bag-icon-link"
              aria-label="Go to shopping bag"
              title="View full bag"
            >
              <ShoppingBag size={18} />
            </Link>

            <button
              className="bag-drawer-trigger"
              onClick={() => setCartOpen(true)}
            >
              <span>Bag</span>
              <b>{itemCount}</b>
            </button>
          </div>
          <button
            className="theme-toggle"
            onClick={toggleAppearance}
            aria-label={`Switch to ${appearance === "light" ? "dark" : "light"} mode`}
            title={`Switch to ${appearance === "light" ? "dark" : "light"} mode`}
          >
            {appearance === "light" ? <Moon size={18} /> : <Sun size={18} />}
          </button>
        </div>
      </header>

      <Routes>
        <Route
          path="/"
          element={
            <HomePage
              books={books}
              query={query}
              setQuery={setQuery}
              loadBooks={loadBooks}
              loading={loading}
              busy={busy}
              addBook={addBook}
            />
          }
        />
        <Route
          path="/books/:id"
          element={<BookDetails addBook={addBook} busy={busy} />}
        />
        <Route
          path="/cart"
          element={
            <CartPage
              cart={cart}
              onQuantity={changeQuantity}
              onRemove={removeBook}
              onCheckout={beginCheckout}
              busy={busy}
            />
          }
        />
        <Route
          path="/checkout"
          element={
            <CheckoutPage cart={cart} onSubmit={placeOrder} busy={busy} />
          }
        />
        <Route
          path="/payment/success"
          element={<PaymentResultPage type="success" />}
        />

        <Route
          path="/payment/cancel"
          element={<PaymentResultPage type="cancel" />}
        />
      </Routes>

      <footer className="site-footer">
        <a className="brand footer-brand" href="#">
          <span className="brand-icon">
            <BookOpen size={18} />
          </span>
          <span className="brand-period">
            bookworm
            <span className="brand-period">.</span>
            <small>THE INDEPENDENT BOOKSHOP</small>
          </span>
        </a>
        <span>Made for the love of a good story.</span>
        <span>© {new Date().getFullYear()} bookworm Books</span>
      </footer>

      {notice && (
        <div className={`toast ${notice.type}`} role="status">
          <span>
            {notice.type === "success" ? (
              <Check size={17} />
            ) : (
              <Sparkles size={17} />
            )}
          </span>
          {notice.text}
          <button onClick={() => setNotice(null)}>
            <X size={15} />
          </button>
        </div>
      )}

      <CartDrawer
        open={cartOpen}
        onClose={() => setCartOpen(false)}
        cart={cart}
        onQuantity={changeQuantity}
        onRemove={removeBook}
        onCheckout={beginCheckout}
      />
      {authOpen && (
        <AuthModal
          mode={authMode}
          onClose={() => setAuthOpen(false)}
          onSuccess={handleAuthSuccess}
          busy={busy}
          setBusy={setBusy}
          setNotice={setNotice}
        />
      )}

      {ordersOpen && (
        <div
          className="modal-backdrop"
          onMouseDown={(e) =>
            e.target === e.currentTarget && setOrdersOpen(false)
          }
        >
          <section className="modal orders-modal">
            <button
              className="icon-button modal-close"
              onClick={() => setOrdersOpen(false)}
            >
              <X />
            </button>
            <div className="eyebrow">YOUR READING HISTORY</div>
            <h2>Your orders</h2>
            {orders.length ? (
              orders.map((order) => (
                <div className="order-card" key={order.orderNumber}>
                  <div className="summary-row">
                    <strong>{order.orderNumber}</strong>
                    <span className="status-pill">{order.status}</span>
                  </div>
                  <div className="summary-row muted">
                    <span>
                      {new Date(order.createdAt).toLocaleDateString()}
                    </span>
                    <strong>{money(order.totalAmount)}</strong>
                  </div>
                  <p className="tiny muted">
                    {order.items
                      ?.map((item) => `${item.bookTitle} × ${item.quantity}`)
                      .join(" · ")}
                  </p>
                </div>
              ))
            ) : (
              <div className="empty-state">
                <BookOpen />
                <h3>No orders yet</h3>
                <p className="muted">
                  Your next favourite is waiting on the shelf.
                </p>
              </div>
            )}
          </section>
        </div>
      )}
    </div>
  );
}
