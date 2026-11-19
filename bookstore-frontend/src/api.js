const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

function readAuthToken() {
  return localStorage.getItem("bookstore_access_token");
}

export async function apiRequest(path, options = {}) {
  const headers = new Headers(options.headers || {});
  if (options.body && !(options.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }

  const token = readAuthToken();
  if (token) headers.set("Authorization", `Bearer ${token}`);

  const cartToken = localStorage.getItem("bookstore_cart_token");
  if (cartToken) headers.set("X-Cart-Token", cartToken);

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers
  });

  if (response.status === 204) return null;

  const raw = await response.text();
  let payload = null;
  if (raw) {
    try { payload = JSON.parse(raw); }
    catch { payload = raw; }
  }

  if (!response.ok) {
    const message =
      (payload && typeof payload === "object" && (payload.message || payload.detail || payload.error)) ||
      (typeof payload === "string" && payload) ||
      `Request failed (${response.status})`;
    throw new Error(message);
  }
  return payload;
}

export const getBooks = (page = 0, size = 20) =>
  apiRequest(`/api/books?page=${page}&size=${size}&sort=title,asc`);

export const searchBooks = (query) =>
  apiRequest(`/api/books/search?searchText=${encodeURIComponent(query)}`);

export const createGuestCart = () =>
  apiRequest("/api/cart/guest", { method: "POST" });

export const getCart = () => apiRequest("/api/cart");

export const addCartItem = (bookId, quantity = 1) =>
  apiRequest("/api/cart/items", {
    method: "POST",
    body: JSON.stringify({ bookId, quantity })
  });

export const updateCartItem = (bookId, quantity) =>
  apiRequest(`/api/cart/items/${bookId}`, {
    method: "PUT",
    body: JSON.stringify({ quantity })
  });

export const removeCartItem = (bookId) =>
  apiRequest(`/api/cart/items/${bookId}`, { method: "DELETE" });

export const register = (name, email, password) =>
  apiRequest("/api/auth/register", {
    method: "POST",
    body: JSON.stringify({ name, email, password })
  });

export const login = (email, password) =>
  apiRequest("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password })
  });

export const checkout = (shippingAddress) =>
  apiRequest("/api/checkout", {
    method: "POST",
    body: JSON.stringify({ shippingAddress })
  });

export const initiatePayment = (orderNumber) =>
  apiRequest(`/api/payments/${encodeURIComponent(orderNumber)}/checkout-session`, {
    method: "POST",
  });

export const getOrders = () => apiRequest("/api/orders");

export const getBookById = (id) =>
  apiRequest(`/api/books/${id}`, { method: "GET" });