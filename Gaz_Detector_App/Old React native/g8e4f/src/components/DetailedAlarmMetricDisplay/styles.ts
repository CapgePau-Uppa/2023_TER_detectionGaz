import styled from 'styled-components/native';
import Colors from '../../constants/Colors';
export const ParameterCard = styled.View`
  border-width: 1px;
  border-color: ${Colors.borders};
  margin-left: 20px;
  width: 22%;
  height: 200px;
  margin-bottom: 40px;
  border-radius: 10px;
  margin-right: 20px;
`;

export const ParameterHeader = styled.View`
  border-bottom-width: 1px;
  padding: 10px 30px;
  flex-direction: row;
  justify-content: center;
  border-color: ${Colors.borders};
`;

export const ParameterHeaderText = styled.Text`
  font-size: 25px;
  color: ${Colors.textColor};
`;

export const MetricView = styled.View`
  flex: 1;
`;
