import React from "react";
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

export default MyButton;
