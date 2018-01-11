package checkers;

import java.util.Vector;

//Класс движений
public class CheckerMove {
        final static int legalMove = 1;
        final static int illegalMove = 2;
        final static int incompleteMove = 3;

    /**
     * Возврат индекса в соответствии с полученными x и y значениями
     */

    static int [] getIndex(int x,int y){
        int [] index=new int [2];
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if(i*50<x && i*50+50>x && j*50<y && j*50+50>y)
                {
                    index[0]=i;
                    index[1]=j;
                    return index;
                }
            }
        }
        return index;
    }

    /*
    noMovesLeft возвращает true если нет больше ходов влево для следующего игрока
    иначе возвращает false
    */
    static boolean noMovesLeft(int[][] board,int toMove)
    {
            for (int i=0; i<8; i++){
                    for (int j=0; j<8; j++){
                        if ( (float)(i+j)/2 != (i+j)/2 )
                        {
                                if (toMove == Checkers.redNormal &&
                                        (board[i][j] == Checkers.redNormal ||
                                        board[i][j] == Checkers.redKing))
                                {
                                        if (canWalk(board,i,j)) return false;
                                        else if (canCapture(board,i,j)) return false;
                                }
                                else if (toMove == Checkers.yellowNormal &&
                                        (board[i][j] == Checkers.yellowNormal ||
                                        board[i][j] == Checkers.yellowKing))
                                {
                                        if (canWalk(board,i,j)) return false;
                                        else if (canCapture(board,i,j)) return false;
                                }
                        }
                    }
            }
            return true;
    }

    /*
     ApplyMove проверяет, является ли введенное изменение законным, незаконным или неполным.
     Если IsMoveLegal возвращае incompleteMove, это значит, что захват только что было сделан.
     Вызоов canCapture() показывает возможен ли следующий захват. очищает старую позицию и
      двигает шашку на новую позицию
    */

    static int ApplyMove(int[][] board,int srtI,int srtJ,int endI,int endJ)
        {
		        int result = isMoveLegal(board,srtI,srtJ,endI,endJ,colour(board[srtI][srtJ]));
                if (result != illegalMove)
                {
                        if ( Math.abs(endI - srtI) == 1)
                        {
                                board[endI][endJ] = board[srtI][srtJ];          //объявляет здесь шашку
                                board[srtI][srtJ] = Checkers.empty;             //очищает предыдущую ячейку.
                        }
                        else // захват
                        {
                                board[(srtI + endI)/2][(srtJ + endJ)/2] = Checkers.empty;
                                board[endI][endJ] = board[srtI][srtJ];
                                board[srtI][srtJ] = Checkers.empty;
                        }

                        if (result == incompleteMove)
                        {
                                //если нет в будущем захватов
                                if (!(canCapture(board,endI,endJ)))
                                        result = legalMove;
                        }

                        // проверка новой дамки
                        if ( board[endI][endJ] == Checkers.redNormal && endJ == 7){
                               board[endI][endJ] = Checkers.redKing;
                        }

                        else if ( board[endI][endJ] == Checkers.yellowNormal && endJ == 0){
                                board[endI][endJ] = Checkers.yellowKing;
                        }


                }
                return result;
        }

    /*
     IsMoveLegal проверяет если движение является легальным.
     Возврашает illegalMove или legalMove
     для сравнения с canCapture(int[][],int,int), чтоб увидеть
      есть ли здесть другие варианты для последующего захвата

     Возвращает incompleteMove если захват был произведен здесь.
     Примечание: он не проверяет возможен ли второй захват!
    */
    static int isMoveLegal(int[][] board,int srtI,int srtJ,int endI,int endJ,int turn)
        {
			if (! (inRange(srtI,srtJ) && inRange(endI,endJ) ) )       //если попытаться выйти за пределы доски,
				return illegalMove;                                           //Возвращает  illegal move
            if (board[endI][endJ] != Checkers.empty)                     //если пытается перейти на занятую клетку
                return illegalMove;                                           //возвращает illegal move

            int piece = board[srtI][srtJ];
            if ( Math.abs(srtI - endI) == 1 )
            {
                // сперва праверяем возможны ли другие возможности захвата
                switch (piece)
                {
                    case Checkers.redNormal:
                    case Checkers.redKing:
                        for (int i=0;i<8;i++){
                            for (int j=0;j<8;j++){
                                if ((board[i][j] == Checkers.redNormal ||
                                        board[i][j] == Checkers.redKing)
                                        && canCapture(board,i,j))
                                    return illegalMove;
                            }
                        }
                        break;
                    case Checkers.yellowNormal:
                    case Checkers.yellowKing:
                        for (int i=0;i<8;i++){
                            for (int j=0;j<8;j++){
                                if ((board[i][j] == Checkers.yellowNormal ||
                                    board[i][j] == Checkers.yellowKing)
                                    && canCapture(board,i,j))
                                    return illegalMove;
                                }
                        }
                        break;
				}

                switch (piece)//проверака направления хода
                {
                    case Checkers.redNormal:
                        if (endJ - srtJ == 1) return legalMove;       //Обычная шашка может толькоидти вперед
                        break;
                    case Checkers.yellowNormal:
                        if (endJ - srtJ == -1) return legalMove;
                        break;
                    case Checkers.redKing:
                    case Checkers.yellowKing:
                        if ( Math.abs(endJ - srtJ) == 1 )return legalMove;    //дамки могут хоить в любом направлении
                        break;
				}
				return illegalMove;

			}

            else if ( Math.abs(srtI - endI) == 2 )
            {
				int cap_i = (srtI + endI) / 2;
                int cap_j = (srtJ + endJ) / 2;
                int cap_piece = board[cap_i][cap_j];

                if (turn == Checkers.redNormal)         // бьем толко шашку оппонента .
                                                          //в противном случае это illegal move.
                {
					if (!(cap_piece == Checkers.yellowNormal ||
						cap_piece == Checkers.yellowKing))
                        return illegalMove;
				}
                else if (!(cap_piece == Checkers.redNormal ||       //то же самое для желтых
					cap_piece == Checkers.redKing))
                    return illegalMove;

				switch (piece)              //если вы пойдете бить, тоход должен быть длиной два
                {
                case Checkers.redNormal:
					if (Math.abs(endJ - srtJ) != 2)
						return illegalMove;
					break;
				case Checkers.yellowNormal:
                    if ((endJ - srtJ) != -2)
						return illegalMove;
					break;
				case Checkers.redKing:
                case Checkers.yellowKing:
					if (Math.abs(endJ - srtJ) != 2)
						return illegalMove;
				}

                return incompleteMove;   // если можно продолжить ход, убить еще одну
			}
//			else if ( Math.abs(srtI - endI) > 2 )
//			{
//                switch (piece)
//                    case
//            }
            return illegalMove;     //если нельзя продолжить ход
        }

        static int isWalkLegal(int[][] board,int srtI,int srtJ,int endI,int endJ)
        {
			if (! (inRange(srtI,srtJ) && inRange(endI,endJ) ) )
				return illegalMove;
            if (board[endI][endJ] != Checkers.empty)
                return illegalMove;

            int piece = board[srtI][srtJ];
            if ( Math.abs(srtI - endI) == 1 )
            {
                switch (piece)
                {
                case Checkers.redNormal:
					if (endJ - srtJ == 1) return legalMove;
					break;
                case Checkers.yellowNormal:
					if (endJ - srtJ == -1) return legalMove;
                    break;
				case Checkers.redKing:
                case Checkers.yellowKing:
                    if ( Math.abs(endJ - srtJ) == 1 ) return legalMove;
                    break;
				}
				return illegalMove;
			}
            return illegalMove;
        }

		static boolean canCapture(int[][] board, int toMove)
		{
			for (int i=0; i<8; i++)
				for (int j=0; j<8; j++)
			{
				if (colour(board[i][j]) == toMove && canCapture(board,i,j))
					return true;
			}
			return false;
		}

        // проверяем доску чтоб увидеть удар ауказанный в(x,y)
        // может походить иначе
        static boolean canCapture(int[][] board, int i, int j)
        {
                switch (board[i][j])
                {
                case Checkers.redNormal:
                        // проверяем есои (красные) шашки выходять с доски
                        if (i+2<8 && j+2<8)    
                                if ( (board[i+1][j+1] == Checkers.yellowNormal ||
                                  board[i+1][j+1] == Checkers.yellowKing)
                                  &&
                                  (board[i+2][j+2] == Checkers.empty))
                                  return true;
                        // другие возможные направления движения
                        if (i-2>-1 && j+2<8)        
                                if ( (board[i-1][j+1] == Checkers.yellowNormal ||
                                board[i-1][j+1] == Checkers.yellowKing)
                                &&
                                board[i-2][j+2] == Checkers.empty)
                                return true;
                        break;
                case Checkers.yellowNormal:
                        if (i+2<8 && j-2>-1)
							if ( (board[i+1][j-1] == Checkers.redNormal ||
                                board[i+1][j-1] == Checkers.redKing)
                                &&
                                board[i+2][j-2] == Checkers.empty)
                                return true;
                        if (i-2>-1 && j-2>-1)
                            if ( (board[i-1][j-1] == Checkers.redNormal ||
                                board[i-1][j-1] == Checkers.redKing)
                                &&
                                board[i-2][j-2] == Checkers.empty)
                                return true;
                        break;
                case Checkers.redKing:   //должен проверить все четыре направления
                        if (i+2<8)
                        {
                                if (j+2<8)
                                        if ( (board[i+1][j+1] == Checkers.yellowNormal ||
                                        board[i+1][j+1] == Checkers.yellowKing )
                                        &&
                                        board[i+2][j+2] == Checkers.empty)
                                        return true;
                                if (j-2>-1)
                                        if ( (board[i+1][j-1] == Checkers.yellowNormal ||
                                        board[i+1][j-1] == Checkers.yellowKing )
                                        &&
                                        board[i+2][j-2] == Checkers.empty)
                                        return true;
                        }
                        if (i-2>-1)
                        {
                                if (j+2<8)
                                        if ( (board[i-1][j+1] == Checkers.yellowNormal ||
                                        board[i-1][j+1] == Checkers.yellowKing )
                                        &&
                                        board[i-2][j+2] == Checkers.empty)
                                        return true;
                                if (j-2>-1)
                                        if ( (board[i-1][j-1] == Checkers.yellowNormal ||
                                        board[i-1][j-1] == Checkers.yellowKing )
                                        &&
                                        board[i-2][j-2] == Checkers.empty)
                                        return true;
                        }
                        break;
                case Checkers.yellowKing:
                        if (i+2<8)
                        {
                                if (j+2<8)
                                        if ( (board[i+1][j+1] == Checkers.redNormal ||
                                        board[i+1][j+1] == Checkers.redKing )
                                        &&
                                        board[i+2][j+2] == Checkers.empty)
                                        return true;
                                if (j-2>-1)
                                        if ( (board[i+1][j-1] == Checkers.redNormal ||
                                        board[i+1][j-1] == Checkers.redKing )
                                        &&
                                        board[i+2][j-2] == Checkers.empty)
                                        return true;
                        }
                        if (i-2>-1)
                        {
                                if (j+2<8)
                                        if ( (board[i-1][j+1] == Checkers.redNormal ||
                                        board[i-1][j+1] == Checkers.redKing )
                                        &&
                                        board[i-2][j+2] == Checkers.empty)
                                        return true;
                                if (j-2>-1)
                                        if ( (board[i-1][j-1] == Checkers.redNormal  ||
                                        board[i-1][j-1] == Checkers.redKing )
                                        &&
                                        board[i-2][j-2] == Checkers.empty)
                                        return true;
                        }
                        break;
                }
                return false;
        }

        // canWalk() возвращает true  если удар можно сделать с (i,j)
        // legal ход без захвата
        // Imporatant to see if the game is over
        static boolean canWalk(int[][] board, int i, int j)
        {
                switch ( board[i][j] )
                {
                case Checkers.redNormal:
                        if ( isEmpty(board,i+1,j+1) || isEmpty(board,i-1,j+1) )
                                return true;
                        break;
                case Checkers.yellowNormal:
                        if ( isEmpty(board,i+1,j-1) || isEmpty(board,i-1,j-1) )
                                return true;
                        break;
                case Checkers.redKing:
                case Checkers.yellowKing:
                        if ( isEmpty(board,i+1,j+1) || isEmpty(board,i+1,j-1)
                                || isEmpty(board,i-1,j+1) || isEmpty(board,i-1,j-1) )
                                return true;
                }
                // board[i][j] не может идти куда либо направо сечас
                return false;
        } 

        private static boolean isEmpty(int[][] board, int i, int j)
        {
                if (i>-1 && i<8 && j>-1 && j<8)
                        if (board[i][j] == Checkers.empty)
                        return true;
                return false;
        }

		// закрашивает цвет после удара
		static int colour(int piece)
		{
			switch (piece)
			{
			case Checkers.redNormal:
			case Checkers.redKing:
				return Checkers.redNormal;
			case Checkers.yellowNormal:
			case Checkers.yellowKing:
				return Checkers.yellowNormal;
		 	}
			return Checkers.empty;
		}

		// проверка вхождения шашки в пределы доски
        private static boolean inRange(int i, int j)
		{
			return (i>-1 && i<8 && j>-1 && j<8);
		}



//
// учитывая доску, генерирует все возможные ходы в зависимости от нее
    static Vector  generateMoves(int[][] board, int turn) {
     Vector moves_list = new Vector();
     int move;

     for (int i=7; i>=0; i--)
       for (int j=0; j<8; j++)
            if (turn==colour(board[i][j])) {
         if (canCapture(board,turn)) {
             for (int k=-2; k<=2; k+=4)     //проверка всех направлений
                 for (int l=-2; l<=2; l+=4)
                         {
                            move=isMoveLegal(board,i,j,i+k,j+l,turn);
                            if (move == incompleteMove)
                            {
                                int[] int_array = new int[4];   //хранит старые координаты и новые координаты
                                int_array[0]=i; int_array[1]=j;
                                int_array[2]=i+k; int_array[3]=j+l;
                                    int[][] temp_board = GameEngine.copyBoard(board);
                                    move = CheckerMove.ApplyMove(temp_board,i,j,i+k,j+l);
                                    if (move == incompleteMove)/*(canCapture(temp_board,i+k,j+l))*/
                                    {
                                        forceCaptures(temp_board, int_array,moves_list,10);
                                    }
                                    else
                                    {
                                        moves_list.addElement(int_array);
                                    }

                            }
                        } 
         }
         else {
                for (int k=-1; k<=2; k+=2)
                    for (int l=-1; l<=2; l+=2){
                        if (inRange(i+k,j+l))
                        {
                            move= isWalkLegal(board,i,j,i+k,j+l);
                            if (move == legalMove)
                            {
                                int[] int_array = new int[4];
                                int_array[0]=i; int_array[1]=j;
                                int_array[2]=i+k; int_array[3]=j+l;
                                    //шаг с возможностью удара
                                    // добавление нового массив к вектору moves_list
                                    moves_list.addElement(int_array);
                            }
                        }
                }
         }
     }
     return moves_list;

    }

//"совершает перемещение" в Minimax.  просто совершает ход
    static void moveComputer(int[][] board, int[] move)
    {
        int startx = move[0];
        int starty = move[1];
        int endx = move[2];
        int endy = move[3];
        while (endx>0 || endy>0)
        {

            ApplyMove(board,startx,starty,endx%10,endy%10);
            startx = endx%10;
            starty = endy%10;
            endx /= 10;
            endy /= 10;
        }
    }

    //Для первоначального захвата, представленного движением, Обноруживет возможность других захватов
    //пока это возможно.  Если имеется несколько конфигураций захвата,
    //добавить все  moves_list
    private static void forceCaptures(int[][] board, int[] move, Vector moves_list,int inc){
     int newx = move[2], newy = move[3];

     while (newx>7 || newy>7){
        newx/=10;
        newy/=10;
     }//в конце
     for (int i=-2; i<=2; i+=4)
       for (int j=-2; j<=2; j+=4)
         if (inRange(newx+i,newy+j)) {
            int[][] tempPosition = GameEngine.copyBoard(board);
            int moveResult = ApplyMove(tempPosition,newx,newy,newx+i,newy+j);
            if (moveResult == legalMove) {      // обычный ход без дополнительных
                int[] new_move = new int[4];
                new_move[0] = move[0];
                new_move[1] = move[1];
                new_move[2] = move[2]+(newx+i)*inc;
                new_move[3] = move[3]+(newy+j)*inc;
                moves_list.addElement(new_move);
            } 
            else if (moveResult == incompleteMove)      //Есть несколько захватов
            {
                int[] new_move = new int[4];
                new_move[0] = move[0];
                new_move[1] = move[1];
                new_move[2] = move[2]+(newx+i)*inc;
                new_move[3] = move[3]+(newy+j)*inc;

                forceCaptures(tempPosition, new_move, moves_list, inc*10);  //до тех пор, пока есть захваты
            }
         }
    }
}
