/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          light: '#c8e6c9',
          DEFAULT: '#4caf50',
          dark: '#388e3c',
        },
        secondary: '#ffc107',
      },
    },
  },
  plugins: [],
}
