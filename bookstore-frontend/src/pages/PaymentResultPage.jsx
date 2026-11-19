import { Link, useSearchParams } from "react-router-dom";
import { CheckCircle2, Clock3, XCircle } from "lucide-react";

export default function PaymentResultPage({ type }) {
  const [params] = useSearchParams();

  const cancelled = type === "cancel";
  const sessionId = params.get("session_id");
  const orderNumber = params.get("orderNumber");

  return (
    <main className="checkout-page">
      <div className="empty-cart-page">
        {cancelled ? (
          <>
            <XCircle size={44} />
            <h2>Payment not completed</h2>
            <p>
              Your order has not been confirmed. You can return to the bookstore
              and retry payment.
            </p>
          </>
        ) : (
          <>
            <Clock3 size={44} />
            <h2>Payment received — confirmation pending</h2>
            <p>
              We're waiting for Stripe to confirm your payment. Your order will
              be confirmed after our server verifies it.
            </p>
            {sessionId && <p className="tiny muted">Session: {sessionId}</p>}
          </>
        )}

        <Link to="/" className="button button-dark">
          Return to bookstore
        </Link>
      </div>
    </main>
  );
}
