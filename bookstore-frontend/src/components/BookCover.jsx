export default function Cover({ book, index = 0, compact = false }) {
  const fallbackCovers = [
    ["#e7d8c6", "#7e4f35"],
    ["#d8e2d4", "#345443"],
    ["#e9d7dc", "#8a5266"],
    ["#d8dfeb", "#425c83"],
    ["#eadfc6", "#826b35"],
    ["#ddd9ed", "#62558b"],
  ];
  const [bg, ink] = fallbackCovers[index % fallbackCovers.length];
  return (
    <div
      className={`book-cover ${compact ? "book-cover-small" : ""}`}
      style={{ "--cover-bg": bg, "--cover-ink": ink }}
    >
      {book.imageUrl ? (
        <img src={book.imageUrl} alt={book.title} />
      ) : (
        <div className="cover-art">
          <span className="cover-mark"></span>
          <span className="cover-title">{book.title}</span>
          <span className="cover-author">{book.author}</span>
          <span className="cover-rule" />
        </div>
      )}
    </div>
  );
}
