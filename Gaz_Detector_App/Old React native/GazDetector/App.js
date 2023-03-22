import React from 'react'

import {Container} from './styles'
import Main from './screens/main'
import {SerialProvider} from './serial'

const App = () => {
  return (
    <SerialProvider>
      <Container>
        <Main />
      </Container>
    </SerialProvider>
  )
}

export default App