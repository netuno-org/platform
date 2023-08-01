import React from "react";
import PropTypes from "prop-types";
import { Button } from 'antd';

function MyButton({ text, onClick }) {
  return (
    <div>
      <Button type="primary" onClick={ onClick }>
        { text }
      </Button>
    </div>
  );
};

MyButton.propTypes = {
  text: PropTypes.string.isRequired,
  onClick: PropTypes.func.isRequired
};

export default MyButton;