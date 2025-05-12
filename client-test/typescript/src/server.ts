import assert from 'node:assert/strict';
import morgon from 'morgan';
import express from 'express';
import bodyParser from 'body-parser';

import type { JavaClass } from './index.java17';

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
