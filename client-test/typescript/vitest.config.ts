import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    globals: true,
    include: ['./tests/**/*.test.tsx', './tests/**/*.test.ts'],
    watch: false,
    poolOptions: {
      threads: {
        singleThread: true,
      }
    },
  },
});
