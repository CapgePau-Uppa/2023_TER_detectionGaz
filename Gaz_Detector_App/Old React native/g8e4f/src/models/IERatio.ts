import SetParameter from '../interfaces/SetParameter';

export class IERatio implements SetParameter {
  public value: number;
  public lowerLimit: number;
  public upperLimit: number;
  public setValue: number;
  public name: string = 'I:E Ratio';
  public unit: string = '';

  constructor(
    public inspiratory: SetParameter,
    public expiratory: SetParameter,
  ) {
    this.value = inspiratory.value / expiratory.value;
    this.lowerLimit = inspiratory.lowerLimit / expiratory.lowerLimit;
    this.upperLimit = inspiratory.upperLimit / expiratory.upperLimit;
    this.setValue = inspiratory.setValue / expiratory.setValue;
  }

  public toString = (): string => {
    return `${this.inspiratory.value} : ${this.expiratory.value}`;
  };
}
