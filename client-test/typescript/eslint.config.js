import js from "@eslint/js";
import tseslint from "typescript-eslint";
import { defineConfig } from "eslint/config";


export default defineConfig([
  { files: ["**/*.{js,mjs,cjs,ts}"], plugins: { js }, extends: ["js/recommended"] },
  tseslint.configs.recommended,
  {
    rules: {
      semi: ["warn", "always"],
      // tsc will handle below:
      "@typescript-eslint/no-unused-vars": "off",
      "no-undef": "off",
      "no-case-declarations": "off",
    }
  }
]);