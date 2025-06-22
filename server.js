const express = require('express');
const bodyParser = require('body-parser');
const mongoose = require('mongoose');
const path = require('path');

// Import our config file
const config = require('./config');

// Define routes
let index = require('./routes/index');
let image = require('./routes/image');

// Connecting to MongoDB Atlas using our config
const environment = process.env.NODE_ENV || 'development';
const mongoURI = config.mongoURI[environment];

console.log(`Connecting to MongoDB in ${environment} mode...`);

mongoose.connect(mongoURI, { useNewUrlParser: true, useUnifiedTopology: true }, (err) => {
    if (err) {
        console.log('MongoDB connection error:', err);
    } else {
        console.log(`Connected to MongoDB Atlas (${environment})`);
    }
});

// Test if the database has connected successfully
let db = mongoose.connection;
db.once('open', () => {
    console.log('Database connected successfully');
});

db.on('error', (err) => {
    console.error('MongoDB connection error:', err);
});

// Initializing the app
const app = express();

// View Engine
app.set('view engine', 'ejs');

// Set up the public folder;
app.use(express.static(path.join(__dirname, 'public')));

// body parser middleware
app.use(express.json())

app.use('/', index);
app.use('/image', image);
 
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
    console.log(`Server is listening at http://localhost:${PORT}`)
});