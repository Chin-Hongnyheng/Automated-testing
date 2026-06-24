import { Injectable, NotFoundException } from '@nestjs/common';

@Injectable()
export class ProductsService {
  private products: any[] = [];
  private idCounter = 1;

  create(dto: any) {
    const product = { id: this.idCounter++, ...dto };
    this.products.push(product);
    return product;
  }

  findAll() {
    return this.products;
  }

  findOne(id: number) {
    const product = this.products.find((p) => p.id === id);
    if (!product) throw new NotFoundException();
    return product;
  }

  update(id: number, dto: any) {
    const product = this.findOne(id);
    Object.assign(product, dto);
    return product;
  }

  remove(id: number) {
    const index = this.products.findIndex((p) => p.id === id);
    if (index === -1) throw new NotFoundException();
    return this.products.splice(index, 1)[0];
  }
}
