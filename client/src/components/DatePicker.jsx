
import React from "react";
import ReactDatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { registerLocale } from "react-datepicker";
import ru from "date-fns/locale/ru";

registerLocale("ru", ru);

const DatePicker = (props) => {
  return <ReactDatePicker {...props} />;
};

export default DatePicker;
