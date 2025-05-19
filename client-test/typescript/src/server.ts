import assert from 'node:assert/strict';
import morgon from 'morgan';
import express from 'express';
import bodyParser from 'body-parser';

import type { ArrayClass, DependencyClassA, JavaClass, JavaRecord, MapClass, MathClass, SubtypeWithNestedCustomTypeString } from './index.java17';

const app = express();
app.use(morgon('dev'));
app.use(bodyParser.json());
app.get('/health', (_, res) => {
  res.send('ok');
});
app.post("/:typeName", (req, res) => {
  if (!req.body) {
    res.status(400);
    res.send("No body");
    return;
  }

  const { typeName } = req.params;
  switch (typeName) {
    case "JavaClass":
      validateJavaClass(req.body);
      break;
    case "JavaTimeClass":
      requireAllFieldNonBlankString(req.body);
      break;
    case "SubtypeWithNestedCustomTypeString":
      validateSubtypeWithNestedCustomTypeString(req.body);
      break;
    case "DependencyClassA":
      validateDependencyClassA(req.body);
      break;
    case "MapClass":
      validateMapClass(req.body);
      break;
    case "ArrayClass":
      validateArrayClass(req.body);
      break;
    case "JavaRecord":
      validateJavaRecord(req.body);
      break;
    case "MathClass":
      validateMathClass(req.body);
      break;
    default:
      res.status(400);
      res.send(`Unknown type: ${typeName}`);
      return;
  }
  res.send(req.body);
});


await app.listen(3005, () => {
  console.log('Server started on port 3005');
});

function validateJavaClass(obj: JavaClass) {
  assert.equal(obj.size, 3, "JavaClass.size");
  assert.equal(obj.a, 555, "JavaClass.a");
  assert.equal(obj.string, "foo", "JavaClass.string");
}

function requireAllFieldNonBlankString(obj: any) {
  for (const key in obj) {
    if (typeof obj[key] !== "string" || obj[key] === "") {
      throw new Error(`Field ${key} is required and cannot be blank`);
    }
  }
}

function validateSubtypeWithNestedCustomTypeString(obj: SubtypeWithNestedCustomTypeString) {
  assert.equal(obj.value.value, "foo", "SubtypeWithNestedCustomTypeString.value.value");
}

function validateDependencyClassA(obj: DependencyClassA) {
  assert.equal(obj.a, 100, "DependencyClassA.a");
  assert.equal(obj.b?.c?.a?.a, 200, "DependencyClassA.b.c.a.a");
}

function validateMapClass(obj: MapClass) {
  assert.deepEqual(obj.mapField, { "5": "bar" });
  assert.deepEqual(obj.enumKeyMapField, { "3": "bar3" });
  assert.deepEqual(obj.customMapField, { "33": "bar22" });
  assert.deepEqual(obj.nestedMapField, { foo: { bar: 5 } });
}

function validateArrayClass(obj: ArrayClass) {
  assert.deepEqual(obj.arr, ["foo", "bar"]);
}

function validateJavaRecord(obj: JavaRecord<string>) {
  assert.equal(obj.string, "exampleString");
  assert.equal(obj.primitiveByte, 1);
  assert.equal(obj.boxedByte, 2);
  assert.equal(obj.primitiveShort, 3);
  assert.equal(obj.boxedShort, 4);
  assert.equal(obj.primitiveInt, 5);
  assert.equal(obj.boxedInt, 6);
  assert.equal(obj.primitiveLong, 7);
  assert.equal(obj.boxedLong, 8);
  assert.equal(obj.primitiveFloat, 9.5);
  assert.equal(obj.boxedFloat, 10.5);
  assert.equal(obj.primitiveDouble, 11.5);
  assert.equal(obj.boxedDouble, 12.5);
  assert.equal(obj.primitiveBoolean, true);
  assert.equal(obj.boxedBoolean, true);
  assert.equal(obj.primitiveChar, 'a');
  assert.equal(obj.boxedChar, 'b');
  assert.equal(obj.object, "object");
  assert.equal(obj.cyclicDependency?.a, 999);
  assert.equal(obj.containerStringList[0]?.t, "bar");
  assert.equal(obj.containerStringListCollection.at(0)?.at(0)?.t, "foo");
  assert.deepEqual(obj.genericList, ["genericValue"]);
  assert.deepEqual(obj.genericSet, ["genericValue"]);
  assert.deepEqual(obj.genericListSet, [["genericValue"]]);
  assert.deepEqual(obj.intArray, [1, 2, 3]);
  assert.deepEqual(obj.boxedIntArray, [4, 5, 6]);
  assert.equal(obj.enumGalaxy, "MilkyWay");
  assert.equal(obj.enumSize, 3);
  assert.equal(obj.duplicateAccessor, "duplicate");
}

function validateMathClass(obj: MathClass) {
  assert.equal(obj.bigDecimal, "1.2345", "MathClass.bigDecimal");
  assert.equal(obj.bigInteger, "123456789", "MathClass.bigInteger");
}