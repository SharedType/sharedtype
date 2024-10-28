/** @type {import('eslint').Linter.Config} */
module.exports = {
  root: true,
  env: { browser: true, es2022: true },
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
  ],
  ignorePatterns: ['dist', '.eslintrc.cjs', '*.config.js', '*.config.ts', '__setup__', "generated", 'tmp'],
  parser: '@typescript-eslint/parser',
  plugins: ['@stylistic'],
  rules: {
    "@typescript-eslint/no-unused-vars": "off", // tsc does the job
    "prefer-destructuring": ["warn", { "object": true }],
    "@stylistic/semi": ["warn", "always"],
    "@stylistic/quotes": ["warn", "double"],
    "@stylistic/comma-dangle": ["warn", "always-multiline"],
    "@stylistic/eol-last": ["warn", "always"],
    "@stylistic/member-delimiter-style": ["warn", {
      "multiline": {
        "delimiter": "comma",
        "requireLast": true
      },
      "singleline": {
        "delimiter": "comma",
        "requireLast": false
      },
    }]
  },
}
