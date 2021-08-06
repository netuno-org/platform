import React from "react";
import PropTypes from "prop-types";
const Input = ({ text, type, id, value, pattern, handleChange }) => (
    <div className="form-group">
        <label htmlFor={id}>{text}</label>
        <input
            type={type}
            className="form-control"
            id={id}
            value={value}
            pattern={pattern}
            onChange={handleChange}
            required
        />
    </div>
);
Input.propTypes = {
    text: PropTypes.string.isRequired,
    type: PropTypes.string.isRequired,
    id: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired,
    pattern: PropTypes.string.isRequired,
    handleChange: PropTypes.func.isRequired
};
export default Input;
