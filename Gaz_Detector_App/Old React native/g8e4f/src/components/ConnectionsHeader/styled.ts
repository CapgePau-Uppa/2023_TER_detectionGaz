import styled from 'styled-components/native';
import Colors from '../../constants/Colors';

export const Header = styled.View`
  flex: 1;
  padding-top: 60px;
  padding-bottom: 30px;
  flex-direction: row;
  background-color: ${Colors.alarmsHeaderBackground};
  justify-content: space-evenly;
  align-items: center;
`;

export const ConnectionContainer = styled.View`
  align-items: center;
  justify-content: center;
  align-self: center;
  flex-direction: column;
  text-align: center;
  flex: 1;
`;

export const ConnectionLabel = styled.Text<{
  fontSize: string;
}>`
  color: ${Colors.connectionLableColor};
  text-align: center;
  width: 80%;
  font-size: ${({ fontSize }) => fontSize};
`;
