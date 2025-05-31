// src/config/axiosClient.js
const axios = require("axios");

const client = axios.create({
    baseURL: "http://localhost:8081", // Spring Boot base URL
    headers: {
        "Content-Type": "application/json"
    },
    timeout: 5000
});

module.exports = client;
