# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: order.spec.ts >> Orders >> order can be fetched
- Location: tests/order.spec.ts:20:7

# Error details

```
SyntaxError: Unexpected token 'T', "This actio"... is not valid JSON
```

# Test source

```ts
  1  | import { test, expect } from "@playwright/test";
  2  | 
  3  | const seedOrder = {
  4  |   product: "Keyboard",
  5  |   quantity: 2,
  6  | };
  7  | 
  8  | test.describe("Orders", () => {
  9  |   let orderId: number;
  10 | 
  11 |   test.beforeEach(async ({ request }) => {
  12 |     const r = await request.post("/orders", { data: seedOrder });
> 13 |     orderId = (await r.json()).id;
     |                ^ SyntaxError: Unexpected token 'T', "This actio"... is not valid JSON
  14 |   });
  15 | 
  16 |   test.afterEach(async ({ request }) => {
  17 |     await request.delete(`/orders/${orderId}`);
  18 |   });
  19 | 
  20 |   test("order can be fetched", async ({ request }) => {
  21 |     const res = await request.get(`/orders/${orderId}`);
  22 |     expect(res.ok()).toBeTruthy();
  23 |   });
  24 | });
  25 | 
```