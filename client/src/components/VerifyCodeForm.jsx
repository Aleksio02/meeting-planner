import { useState, useRef } from "react";
import "../styles/VerifyPage.css";

export default function VerifyCodeForm() {
  const [code, setCode] = useState(["", "", "", ""]);
  const inputsRef = useRef([]);

  const handleChange = (value, index) => {
    if (!/^[0-9]?$/.test(value)) return;

    const newCode = [...code];
    newCode[index] = value;
    setCode(newCode);

    if (value && index < 3) {
      inputsRef.current[index + 1].focus();
    }
  };

  const handleKeyDown = (e, index) => {
    if (e.key === "Backspace" && !code[index] && index > 0) {
      inputsRef.current[index - 1].focus();
    }
  };

  const handleSubmit = () => {
    const finalCode = code.join("");
    console.log("Код:", finalCode);
  };

  return (
    <>
      <div className="verify-code-container">
        {code.map((digit, index) => (
          <input
            key={index}
            ref={(el) => (inputsRef.current[index] = el)}
            type="text"
            maxLength="1"
            value={digit}
            onChange={(e) => handleChange(e.target.value, index)}
            onKeyDown={(e) => handleKeyDown(e, index)}
            className="verify-code-input"
          />
        ))}
      </div>

      <button className="verify-button" onClick={handleSubmit}>
        Подтвердить
      </button>
    </>
  );
}