import java.util.Scanner;


class Node          //defines the Node
{
    int value, height;
    Node left, right;

    public Node (int value)         //[left - data - right]
    {                               //[null - value - null]
        this.value = value;
        left = null;
        right = null;
        this.height = 1;
    }
}

public class AVL
{
    static boolean found = false;

    static int height(Node node)            //this gives the height of the tree
    {
        if(node == null)
        {
            return 0;
        }
        return node.height;
    }

    static int BalanceFactor (Node node)            //this gives the balance factor
    {
        if(node == null)            //0 -> perfectly balanced; -1/+1 -> balanced; >+1 or <-1 -> not balanced
        {
            return 0;
        }
        return height(node.left) - height(node.right);          //left height - right height
    }

    static Node rightRotate (Node node)         //LL fixing; tree is left heavy
    {
        Node lf = node.left;           //lf is the left child of node
        Node temp = lf.right;           //temp is the right subtree of lf (it'll be moved later)
        lf.right = node;            //lf becomes new root
        node.left = temp;           //original node becomes right child of lf; temp becomes left child of new node

        node.height = Math.max(height(node.left), height(node.right)) + 1;          //we update the heights of the tree (first the lower one, left and then the right one; max fucnton is used to take the greater one of the two); +1 for the current node
        lf.height = Math.max(height(lf.left), height(lf.right)) + 1;            //same as the above, but for the lf node

        return lf;
    }

    static Node leftRotate (Node node)          //RR ficing; tree is right heavy
    {
        Node rg = node.right;           //rg is the right child of node
        Node temp = rg.left;            //temp is the left subtree of rg (to be moved)
        rg.left = node;         //rg becomes the new root
        node.right =  temp;         //original node becomes the left child of rg; temp becomes the rigth child of the new node

        node.height = Math.max(height(node.left), height(node.right)) + 1;          //we update the heights of the tree (first the lower one, left and then the right one; max fucnton is used to take the greater one of the two); +1 for the current node
        rg.height = Math.max(height(rg.left), height(rg.right)) + 1;            //same as the above, but for the rg node

        return rg;
    }

    static Node insert (Node node, int data)            //insert a node
    {
        if(node == null)                //we make a new node with the given data if there isn't any existing
        {
            return new Node(data);
        }

        if (data < node.value)          //if the input data is smaller than current node, we inseert it towards left
        {
            node.left = insert (node.left, data);           //here we write node.left = ...., as we are trying to attch the  new value to the tree and not overwrite it using node = ....
        }
        else if (data > node.value)         //if the input data is larger than current node, we inseert it towards right
        {
            node.right = insert (node.right, data);
        }
        else
        {
            System.out.println("Duplicate nodes not allowed!");
            return node;
        }
        node.height = (Math.max(height(node.left), (height(node.right)))) + 1;              //updates the height of the node given

        int balfac = BalanceFactor(node);           //updates the balance factor of the tree
    
        //LL case
        if(balfac > 1 && data < node.left.value)            //if the tree is unstable and the enteretd data goes to the left of the tree (left heavy case)
        {
            return rightRotate(node);           //right rotate once
        }

        //RR case
        if(balfac < -1 && data > node.right.value)           //if the tree is unstable and the enteretd data goes to the right of the tree (right heavy case)
        {
            return leftRotate(node);            //left rotate once
        }

        //LR case
        if(balfac > 1 && data > node.left.value)            //the tree is left heavy but the element is being inserted towrasd the right of a left subclass node
        {
            node.left = leftRotate(node.left);
            return (rightRotate(node));
            //left rotate -> right rotate
        }

        //RL case
        if(balfac < -1 && data < node.right.value)          //the tree is right heavy but the leemnt is beign inserted towards the left of a right subclass node
        {
            node.right = rightRotate(node.right);
            return leftRotate(node);     
            //right rotate -> left rotate
        }

        return node;
    }


    static Node min (Node root)           //finds min value, this is needed to find the inordr successor of node, which in ifact is the minimum elemnt on the right side of the node
    {
        while(root.left != null)
        {
            root = root.left;
        }
        return root;
    }

    static Node delete (Node root, int value)
    {
        if(root == null)
        {
            return null;
        }
        if(value < root.value)           //if the value is smaller, it searches for it in left of the root
        {
            root.left = delete(root.left, value);
        }
        else if(value > root.value)          //if the value is larger, it searches for it in the right side of the root
        {
            root.right = delete(root.right, value);
        }
        else            //this is the case were Node value is found
        {
            found = true;
            //Case 1:   Leaf node deletion
            if(root.left == null && root.right == null)         //deletes the node
            {
                root = null;
            }

            //Case 2:   Node with one child
            else if(root.left == null)           //node with only right child
            {
                root = root.right;          //replaces node with its child (right)
            }
            else if(root.right == null)          //node with only left child
            {
                root = root.left;           //replaces node with its child (left)
            }
            else
            {
                //Case 3:   Node with two children
                Node min_node = min(root.right);            //minimum node of the right (larger) side; basically the inorder successor of the node which is infact the minimum element of the right side of the tree
                root.value = min_node.value;          //we replace the node with the inorder successor
                root.right = delete(root.right, min_node.value);         //we then delete the inorder successor from its original location in the tree
            }
        }

        if(root == null)            //if the tree becomes empty
        {
            return null;
        }

        root.height = Math.max(height(root.left), height(root.right)) + 1;          //updates height
        int balfact = BalanceFactor(root);          //gets the balance factor of the updated binary tree; height(left) - height(right)

        //Re-balancing the AVL tree
        
        //If LL balance
        if(balfact > 1 && BalanceFactor(root.left) >= 0)        //left-heavy; child subtree leans left
        {
            return rightRotate(root);
        }

        //If RR balance
        if(balfact < -1 && BalanceFactor(root.right) <= 0)          //right- heavy; child subtree leans right
        {
            return leftRotate(root);
        }

        //If LR balance
        if(balfact > 1 && BalanceFactor(root.left) < 0)         //left-heavy; child subtree leans right
        {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }

        //If RL balance
        if(balfact < -1 && BalanceFactor(root.right) > 0)           //right-heavy; child subtree leans left
        {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }


    static void inOrder(Node root)          //inorder traversal: LNR
    {
        if(root == null)
        {
            return;
        }
        inOrder(root.left);             //visits the left child of the root, the fucntion is called and it prints that value
        System.out.print(root.value + " ");              //prints the value of the current root
        inOrder(root.right);            //visits the right child of the root, the fucntion is called and it prints that value
    }

    static void showBalanceFac (Node root)          //this shows the balancefactor only for the root node element
    {
        if(root == null)
        {
            System.out.println("Tree is empty!");
        }
        else
        {
            System.out.println("Balance factor of the root: " +BalanceFactor(root));
        }
    }

    public static void main (String [] args)
    {
        Scanner sc = new Scanner (System.in);
        Node root = null;
        int val, del, ch;
        do
        {
            System.out.println("1.  Insert");
            System.out.println("2.  Delete");
            System.out.println("3.  Display");
            System.out.println("4.  Exit");
            while(!sc.hasNextInt())
            {
                System.out.println("Invalid input! Enter integer 1 to 4.");
                sc.next();
            }
            ch = sc.nextInt();
            switch (ch)
            {
                case 1:
                {
                    System.out.println("Enter the element: ");
                    while(!sc.hasNextInt())
                    {
                        System.out.println("Invalid input! Enter integer.");
                        sc.next();
                    }
                    val = sc.nextInt();
                    root = insert(root, val);
                    showBalanceFac(root);
                    break;
                }

                case 2:
                {
                    if(root == null)
                    {
                        System.out.println("Tree is empty!!");
                    }
                    else
                    {
                        System.out.println("Enter element to be deleted: ");
                        while(!sc.hasNextInt())
                        {
                            System.out.println("Invalid input! Enter integer.");
                            sc.next();
                        }
                        del = sc.nextInt();
                        found = false;
                        root = delete(root, del);
                        showBalanceFac(root);
                        if(!found)
                        {
                            System.out.println("Element not found!");
                        }
                    }
                    break;
                }

                case 3:
                {
                    if(root == null)
                    {
                        System.out.println("Tree is empty!!");
                    }
                    else
                    {
                        System.out.println("Inorder traversal of AVL: ");
                        inOrder(root);
                        System.out.println();
                    }
                    break;
                }

                case 4:
                {
                    System.out.println("Exiting!");
                    System.exit(0);
                }

                default:
                {
                    System.out.println("Invalid input!");
                    break;
                }
            }
        } while(ch != 4);
    }
}