import styled from 'styled-components/native';
import Colors from '../../constants/Colors';

export const MetricContainer = styled.View`
  flex-direction: row;
  flex: 1;
`;

export const CurrentValueContainer = styled.View`
  flex: 1;
  justify-content: center;
`;

export const Value = styled.Text<{
  color: string;
}>`
  color: ${({ color }) => color};
  font-size: 35px;
  margin-top: 5px;
`;

export const Unit = styled.Text`
  margin-top: 25px;
  margin-left: 5px;
  font-size: 15px;
  color: ${Colors.textColor};
`;
export const LimitsContainer = styled.View`
  flex: 1;
`;

export const ValueWrapper = styled.View`
  justify-content: center;
  flex-direction: row;
  flex-wrap: wrap;
  flex: 1;
`;

export const LimitWrapper = styled.View`
  flex: 1;
  justify-content: center;
`;

export const LimitValue = styled.Text`
  text-align: center;
  font-size: 25px;
  color: ${Colors.warningText};
`;

export const PresetValue = styled.Text`
  text-align: center;
  font-size: 25px;
  color: ${Colors.valueColor};
`;
