import { expect, test } from 'vitest'
import {
  FLOAT_VALUE, LONG_VALUE, MyEnumConstants, REFERENCED_LOCAL_VALUE, SELF_REFERENCED_LOCAL_VALUE, REFERENCED_IMPORTED_VALUE,
  REFERENCED_NESTED_VALUE, STATIC_FIELD_FROM_JAVA_RECORD,
} from '../src/index.java17';

test('Constant values', () => {
  expect(FLOAT_VALUE).toBe(1.888);
  expect(LONG_VALUE).toBe(999);
  expect(MyEnumConstants.INT_VALUE).toBe(1);
  expect(MyEnumConstants.STR_VALUE).toBe("abc");
  expect(STATIC_FIELD_FROM_JAVA_RECORD).toBe(888);
  expect(REFERENCED_LOCAL_VALUE).toBe(555);
  expect(SELF_REFERENCED_LOCAL_VALUE).toBe(555);
  expect(REFERENCED_IMPORTED_VALUE).toBe(666);
  expect(REFERENCED_NESTED_VALUE).toBe(777);
});
