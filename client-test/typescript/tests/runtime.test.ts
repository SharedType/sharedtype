import { expect, test } from 'vitest'
import { MyConstants, MyEnumConstants } from '../src/index.java17';

test('Constant values', () => {
  expect(MyConstants.FLOAT_VALUE).toBe(1.888);
  expect(MyConstants.LONG_VALUE).toBe(999);
  expect(MyEnumConstants.INT_VALUE).toBe(1);
  expect(MyEnumConstants.STR_VALUE).toBe("abc");
});
