import VerifyCodeForm from "../components/VerifyCodeForm";
import "../styles/VerifyPage.css";

function maskEmail(email) {
  const visiblePart = email.slice(-16); // последние 16 символов
  const hiddenLength = email.length - 16;

  if (hiddenLength <= 0) return email;

  return "*".repeat(hiddenLength) + visiblePart;
}

export default function VerifyPage() {
  const email = "vanyalisenko1985@gmail.com";
  const maskedEmail = maskEmail(email);

  return (
    <div className="verify-page">
      <div className="verify-card">
        <h2 className="verify-title">Подтверждение почты</h2>

        <p className="verify-subtitle">
          Код отправлен на:
        </p>

        <p className="verify-email">{maskedEmail}</p>

        <VerifyCodeForm />

        <div className="verify-footer">
          <span className="verify-footer-text">
            Не получили код?
          </span>
          <span className="verify-footer-link">
            Отправить снова
          </span>
        </div>
      </div>
    </div>
  );
}