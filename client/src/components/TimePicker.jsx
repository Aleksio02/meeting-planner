import React, { useState, useEffect, useRef } from "react";
import "../styles/TimePicker.css";

const TimePicker = ({ options, onChange }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [inputValue, setInputValue] = useState("");
  const containerRef = useRef(null);

  const handleFocus = () => setIsOpen(true);

  const handleInputChange = (e) => {
    let val = e.target.value.replace(/[^\d]/g, "").slice(0, 4);

    let hours = val.slice(0, 2);
    let minutes = val.slice(2, 4);

    if (hours.length === 2 && parseInt(hours) > 23) {
      hours = "23";
    }
    if (minutes.length === 2 && parseInt(minutes) > 59) {
      minutes = "59";
    }

    const formatted = [hours, minutes].filter(Boolean).join(":");
    setInputValue(formatted);
    onChange(formatted);
  };

  const handleSelect = (value) => {
    setInputValue(value);
    onChange(value);
    setIsOpen(false);
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        containerRef.current &&
        !containerRef.current.contains(event.target)
      ) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [isOpen]);

  const filteredOptions = options.filter((time) => time.startsWith(inputValue));

  return (
    <div className="timepicker-container" ref={containerRef}>
      <input
        type="text"
        className="timepicker-input"
        value={inputValue}
        onChange={handleInputChange}
        onFocus={handleFocus}
        placeholder="00:00"
        autoComplete="off"
      />
      {isOpen && filteredOptions.length > 0 && (
        <ul className="timepicker-list">
          {filteredOptions.map((time) => (
            <li
              key={time}
              className="timepicker-item"
              onClick={() => handleSelect(time)}
            >
              {time}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default TimePicker;
