import {GeldbetragPipe} from './geldbetrag.pipe';

describe('GeldbetragPipe', () => {

  const pipe = new GeldbetragPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  describe('transform', () => {
    it('it should return correct string on correct input', () => {
      expect(pipe.transform(200)).toEqual("2,00");
    });

    it('it should return correct string on single-digit input', () => {
      expect(pipe.transform(2)).toEqual("0,02");
    });

    it('it should return correct string on two-digit input', () => {
      expect(pipe.transform(20)).toEqual("0,20");
    });

    it('it should return correct string on non-zero decimal input', () => {
      expect(pipe.transform(20.79845)).toEqual("0,20");
    });
  })
});
