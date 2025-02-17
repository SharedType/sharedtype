import { expect, test } from 'vitest'
import { FLOAT_VALUE, LONG_VALUE, MyEnumConstants } from '../src/index.java17';

test('Constant values', () => {
  expect(FLOAT_VALUE).toBe(1.888);
  expect(LONG_VALUE).toBe(999);
  expect(MyEnumConstants.INT_VALUE).toBe(1);
  expect(MyEnumConstants.STR_VALUE).toBe("abc");
});
