import { Injectable, NotFoundException } from '@nestjs/common';

@Injectable()
export class OrdersService {
  private orders: any[] = [];
  private idCounter = 1;

  create(dto: any) {
    const order = { id: this.idCounter++, ...dto };
    this.orders.push(order);
    return order;
  }

  findAll() {
    return this.orders;
  }

  findOne(id: number) {
    const order = this.orders.find((o) => o.id === id);
    if (!order) throw new NotFoundException();
    return order;
  }

  update(id: number, dto: any) {
    const order = this.findOne(id);
    Object.assign(order, dto);
    return order;
  }

  remove(id: number) {
    const index = this.orders.findIndex((o) => o.id === id);
    if (index === -1) throw new NotFoundException();
    return this.orders.splice(index, 1)[0];
  }
}
