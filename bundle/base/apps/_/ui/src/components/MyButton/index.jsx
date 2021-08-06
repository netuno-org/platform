import React from "react";
import PropTypes from "prop-types";
import Button from 'antd/lib/button';

const MyButton = ({ buttonRef, text, click }) => (
    <div ref={ buttonRef }><Button type="primary" onClick={ click }>
        { text }
    </Button></div>
);

MyButton.propTypes = {
    buttonRef: PropTypes.shape({ current: PropTypes.instanceOf(Element) }),
    text: PropTypes.string.isRequired,
    click: PropTypes.func.isRequired
};

export default MyButton;
