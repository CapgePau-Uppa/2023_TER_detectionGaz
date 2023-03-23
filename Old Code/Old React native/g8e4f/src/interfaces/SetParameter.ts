import Parameter from './Parameter';

export default interface SetParameter extends Parameter {
  setValue: number;
  setValueText?: string;
}
